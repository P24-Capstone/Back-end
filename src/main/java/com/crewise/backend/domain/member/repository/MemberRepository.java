package com.crewise.backend.domain.member.repository;

import com.crewise.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    List<Member> findByTeamIdAndMemStateOrderByMemRoleAsc(String teamId, String memState);

    List<Member> findByTeamIdAndMemStateOrderByRegDtmDesc(String teamId, String memState);

    // 사용자가 가입한(승인된) 모임 목록
    List<Member> findByUserIdAndMemState(String userId, String memState);

    Optional<Member> findByUserIdAndTeamId(String userId, String teamId);

    boolean existsByUserIdAndTeamId(String userId, String teamId);

    boolean existsByUserIdAndTeamIdAndMemStateIn(String userId, String teamId, List<String> memStates);

    List<Member> findByTeamIdOrderByMemRoleAsc(String teamId);
}