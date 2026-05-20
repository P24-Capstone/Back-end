package com.crewise.backend.domain.dashboard.service;

import com.crewise.backend.domain.dashboard.dto.DashboardLeaderResponse;
import com.crewise.backend.domain.dashboard.dto.DashboardLeaderResponse.HardMission;
import com.crewise.backend.domain.dashboard.dto.DashboardLeaderResponse.WeeklyPoint;
import com.crewise.backend.domain.dashboard.dto.DashboardMemberResponse;
import com.crewise.backend.domain.dashboard.dto.DashboardMemberResponse.RankItem;
import com.crewise.backend.domain.dashboard.dto.DashboardMemberResponse.TypeCount;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.mission.entity.Mission;
import com.crewise.backend.domain.mission.entity.MissionVerify;
import com.crewise.backend.domain.mission.repository.MissionRepository;
import com.crewise.backend.domain.mission.repository.MissionVerifyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final DateTimeFormatter DTM_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MissionRepository missionRepository;
    private final MissionVerifyRepository missionVerifyRepository;
    private final MemberRepository memberRepository;

    private void checkTeamMember(String userId, String teamId) {
        if (!memberRepository.existsByUserIdAndTeamId(userId, teamId)) {
            throw new IllegalArgumentException("해당 모임의 멤버가 아닙니다.");
        }
    }

    private void checkLeader(String userId, String teamId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."));
        if (!"L".equals(member.getMemRole())) {
            throw new IllegalArgumentException("모임장만 접근 가능합니다.");
        }
    }

    // ─── 개인 대시보드 ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public DashboardMemberResponse getDashboardMember(String teamId, String userId) {
        checkTeamMember(userId, teamId);

        List<Mission> missions = missionRepository.findByTeamIdOrderByMissionIdDesc(teamId);
        List<Long> missionIds = missions.stream().map(Mission::getMissionId).collect(Collectors.toList());
        Map<Long, Mission> missionById = missions.stream()
                .collect(Collectors.toMap(Mission::getMissionId, m -> m));

        List<MissionVerify> allVerifies = missionIds.isEmpty()
                ? Collections.emptyList()
                : missionVerifyRepository.findByMissionIdIn(missionIds);

        List<MissionVerify> myVerifies = allVerifies.stream()
                .filter(v -> userId.equals(v.getMemId()))
                .collect(Collectors.toList());
        List<MissionVerify> myApproved = myVerifies.stream()
                .filter(v -> "A".equals(v.getVerifyState()))
                .collect(Collectors.toList());

        // 1. Streak
        int streak = calcStreak(myApproved);

        // 2. Mission type preference
        Map<String, Long> typeCntMap = myApproved.stream()
                .map(v -> missionById.getOrDefault(v.getMissionId(), null))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Mission::getMissionType, Collectors.counting()));

        Map<String, String> typeLabels = Map.of("I", "AI 인증", "B", "수동 인증", "T", "자유형식");
        List<TypeCount> typeCounts = typeCntMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> TypeCount.builder()
                        .type(e.getKey())
                        .label(typeLabels.getOrDefault(e.getKey(), e.getKey()))
                        .count(e.getValue().intValue())
                        .build())
                .collect(Collectors.toList());

        String topTypeLabel = typeCounts.isEmpty() ? "없음" : typeCounts.get(0).getLabel();

        // 3. Average certification time (mission start → verify submit)
        double avgCertHours = calcAvgCertHours(myApproved, missionById);

        // 4. Ranking among active members
        List<Member> activeMembers = memberRepository
                .findByTeamIdAndMemStateOrderByMemRoleAsc(teamId, "A");

        Map<String, Long> approvedByMember = allVerifies.stream()
                .filter(v -> "A".equals(v.getVerifyState()))
                .collect(Collectors.groupingBy(MissionVerify::getMemId, Collectors.counting()));

        List<Map.Entry<String, Long>> ranked = approvedByMember.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        // include members with 0 approvals
        Set<String> rankedIds = ranked.stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        for (Member m : activeMembers) {
            if (!rankedIds.contains(m.getUserId())) {
                ranked.add(Map.entry(m.getUserId(), 0L));
            }
        }

        Map<String, String> nicByUserId = activeMembers.stream()
                .collect(Collectors.toMap(Member::getUserId, Member::getMemNic));

        int myRank = 1;
        int myApprovedCount = approvedByMember.getOrDefault(userId, 0L).intValue();
        List<RankItem> topRanks = new ArrayList<>();

        for (int i = 0; i < ranked.size(); i++) {
            String uid = ranked.get(i).getKey();
            int cnt = ranked.get(i).getValue().intValue();
            if (uid.equals(userId)) myRank = i + 1;
            if (i < 3) {
                topRanks.add(RankItem.builder()
                        .rank(i + 1)
                        .memNic(nicByUserId.getOrDefault(uid, uid))
                        .approvedCount(cnt)
                        .isMe(uid.equals(userId))
                        .build());
            }
        }

        String myMemNic = nicByUserId.getOrDefault(userId, userId);

        return DashboardMemberResponse.builder()
                .streak(streak)
                .typeCounts(typeCounts)
                .topTypeLabel(topTypeLabel)
                .avgCertHours(Math.round(avgCertHours * 10.0) / 10.0)
                .topRanks(topRanks)
                .myRank(myRank)
                .myApprovedCount(myApprovedCount)
                .myMemNic(myMemNic)
                .build();
    }

    // ─── 모임장 대시보드 ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public DashboardLeaderResponse getDashboardLeader(String teamId, String userId) {
        checkLeader(userId, teamId);

        List<Mission> missions = missionRepository.findByTeamIdOrderByMissionIdDesc(teamId);
        List<Long> missionIds = missions.stream().map(Mission::getMissionId).collect(Collectors.toList());
        Map<Long, Mission> missionById = missions.stream()
                .collect(Collectors.toMap(Mission::getMissionId, m -> m));

        List<MissionVerify> allVerifies = missionIds.isEmpty()
                ? Collections.emptyList()
                : missionVerifyRepository.findByMissionIdIn(missionIds);

        List<Member> activeMembers = memberRepository
                .findByTeamIdAndMemStateOrderByMemRoleAsc(teamId, "A");
        int memberCount = Math.max(activeMembers.size(), 1);

        // 2-1. Overall achievement rate
        long approvedCount = allVerifies.stream()
                .filter(v -> "A".equals(v.getVerifyState())).count();
        int target = missions.size() * memberCount;
        double overallRate = target > 0 ? Math.min(100, approvedCount * 100.0 / target) : 0;

        // 2-2. Weekly trend (last 8 weeks)
        List<WeeklyPoint> weeklyTrend = calcWeeklyTrend(missions, allVerifies, memberCount);

        // 2-3. Hardest missions top 3
        List<HardMission> hardMissions = calcHardMissions(missions, allVerifies, memberCount);

        // 2-4. Heatmap
        int[][] heatmap = calcHeatmap(allVerifies);

        // 3-1 / 3-2. AI stats
        List<MissionVerify> aiVerifies = allVerifies.stream()
                .filter(v -> {
                    Mission m = missionById.get(v.getMissionId());
                    return m != null && "I".equals(m.getMissionType());
                })
                .collect(Collectors.toList());

        int totalAi = aiVerifies.size();
        long aiPassed = aiVerifies.stream()
                .filter(v -> "N".equals(v.getAiRejectYn()) && "A".equals(v.getVerifyState()))
                .count();
        long aiRejected = aiVerifies.stream()
                .filter(v -> "Y".equals(v.getAiRejectYn()))
                .count();

        double aiSuccessRate = totalAi > 0 ? Math.round(aiPassed * 1000.0 / totalAi) / 10.0 : 0;
        double fallbackRate  = totalAi > 0 ? Math.round(aiRejected * 1000.0 / totalAi) / 10.0 : 0;

        return DashboardLeaderResponse.builder()
                .totalMissions(missions.size())
                .totalMembers(memberCount)
                .overallAchievementRate(Math.round(overallRate * 10) / 10.0)
                .weeklyTrend(weeklyTrend)
                .hardMissions(hardMissions)
                .heatmap(heatmap)
                .aiSuccessRate(aiSuccessRate)
                .fallbackRate(fallbackRate)
                .build();
    }

    // ─── 내부 계산 헬퍼 ───────────────────────────────────────────────────────────

    private int calcStreak(List<MissionVerify> approved) {
        Set<LocalDate> dates = approved.stream()
                .map(v -> LocalDate.parse(v.getVerifyRegDtm().substring(0, 10)))
                .collect(Collectors.toSet());
        int streak = 0;
        LocalDate day = LocalDate.now();
        while (dates.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }

    private double calcAvgCertHours(List<MissionVerify> approved, Map<Long, Mission> missionById) {
        List<Double> hours = approved.stream()
                .filter(v -> missionById.containsKey(v.getMissionId()))
                .map(v -> {
                    try {
                        LocalDateTime start = LocalDateTime.parse(
                                missionById.get(v.getMissionId()).getMissionStartDtm(), DTM_FMT);
                        LocalDateTime end = LocalDateTime.parse(v.getVerifyRegDtm(), DTM_FMT);
                        double h = java.time.Duration.between(start, end).toMinutes() / 60.0;
                        return h >= 0 ? h : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return hours.isEmpty() ? 0 : hours.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private List<WeeklyPoint> calcWeeklyTrend(List<Mission> missions,
                                               List<MissionVerify> allVerifies,
                                               int memberCount) {
        LocalDate today = LocalDate.now();
        List<WeeklyPoint> result = new ArrayList<>();

        for (int w = 7; w >= 0; w--) {
            LocalDate weekStart = today
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .minusWeeks(w);
            LocalDate weekEnd = weekStart.plusDays(6);
            String wsStr = weekStart.atStartOfDay().format(DTM_FMT);
            String weStr = weekEnd.atTime(23, 59, 59).format(DTM_FMT);

            long weekMissions = missions.stream()
                    .filter(m -> m.getMissionStartDtm().compareTo(weStr) <= 0
                            && m.getMissionEndDtm().compareTo(wsStr) >= 0)
                    .count();

            long weekApproved = allVerifies.stream()
                    .filter(v -> "A".equals(v.getVerifyState())
                            && v.getVerifyRegDtm().compareTo(wsStr) >= 0
                            && v.getVerifyRegDtm().compareTo(weStr) <= 0)
                    .count();

            long t = weekMissions * memberCount;
            double rate = t > 0 ? Math.min(100, weekApproved * 100.0 / t) : 0;

            String label = weekStart.format(DateTimeFormatter.ofPattern("M/d"));
            result.add(WeeklyPoint.builder()
                    .weekLabel(label)
                    .rate(Math.round(rate * 10) / 10.0)
                    .build());
        }
        return result;
    }

    private List<HardMission> calcHardMissions(List<Mission> missions,
                                                List<MissionVerify> allVerifies,
                                                int memberCount) {
        Map<Long, Long> approvedByMission = allVerifies.stream()
                .filter(v -> "A".equals(v.getVerifyState()))
                .collect(Collectors.groupingBy(MissionVerify::getMissionId, Collectors.counting()));

        return missions.stream()
                .map(m -> {
                    long approved = approvedByMission.getOrDefault(m.getMissionId(), 0L);
                    double failRate = ((memberCount - approved) * 100.0) / memberCount;
                    failRate = Math.max(0, Math.min(100, failRate));
                    return HardMission.builder()
                            .missionTitle(m.getMissionTitle())
                            .failRate(Math.round(failRate * 10) / 10.0)
                            .build();
                })
                .sorted(Comparator.comparingDouble(HardMission::getFailRate).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private int[][] calcHeatmap(List<MissionVerify> allVerifies) {
        int[][] heatmap = new int[7][24];
        for (MissionVerify v : allVerifies) {
            if (!"A".equals(v.getVerifyState())) continue;
            try {
                LocalDateTime dt = LocalDateTime.parse(v.getVerifyRegDtm(), DTM_FMT);
                int day = dt.getDayOfWeek().getValue() - 1; // 0=Mon
                int hour = dt.getHour();
                heatmap[day][hour]++;
            } catch (Exception ignored) {}
        }
        return heatmap;
    }
}
