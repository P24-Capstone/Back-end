package com.crewise.backend.domain.news.service;

import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.news.dto.CommentCreateRequest;
import com.crewise.backend.domain.news.dto.CommentResponse;
import com.crewise.backend.domain.news.dto.NewsResponse;
import com.crewise.backend.domain.news.entity.Comment;
import com.crewise.backend.domain.news.entity.News;
import com.crewise.backend.domain.news.repository.CommentRepository;
import com.crewise.backend.domain.news.repository.NewsRepository;
import com.crewise.backend.domain.user.repository.UserImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final UserImgRepository userImgRepository;

    // 팀 멤버 확인
    private void checkTeamMember(String userId, String teamId) {
        if (!memberRepository.existsByUserIdAndTeamId(userId, teamId)) {
            throw new IllegalArgumentException("해당 모임의 멤버가 아닙니다.");
        }
    }

    // 최근 소식 목록 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public List<NewsResponse> getNewsList(String teamId, String userId) {
        checkTeamMember(userId, teamId);
        return newsRepository.findByTeamIdOrderByNewsIdDesc(teamId)
                .stream()
                .map(NewsResponse::from)
                .collect(Collectors.toList());
    }

    // 내가 가입한 모든 팀의 최신 소식 목록 조회
    @Transactional(readOnly = true)
    public List<NewsResponse> getAllNewsList(String userId) {
        List<String> teamIds = memberRepository.findByUserIdAndMemState(userId, "A")
                .stream()
                .map(m -> m.getTeamId())
                .collect(Collectors.toList());

        if (teamIds.isEmpty()) {
            return List.of();
        }

        return newsRepository.findByTeamIdInOrderByNewsIdDesc(teamIds)
                .stream()
                .map(NewsResponse::from)
                .collect(Collectors.toList());
    }

    // 최근 소식 생성 (내부 호출용 - 다른 서비스에서 이벤트 발생 시)
    @Transactional
    public NewsResponse createNews(String targetType, Long targetId, String newsContent, String teamId) {
        News news = News.builder()
                .targetType(targetType)
                .targetId(targetId)
                .newsContent(newsContent)
                .teamId(teamId)
                .build();
        return NewsResponse.from(newsRepository.save(news));
    }

    // 댓글 목록 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long newsId, String userId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소식입니다."));
        checkTeamMember(userId, news.getTeamId());

        // 댓글은 M(신규멤버), A(미션인증) 타입만 허용
        if (!"M".equals(news.getTargetType()) && !"A".equals(news.getTargetType())) {
            throw new IllegalArgumentException("댓글을 달 수 없는 소식입니다.");
        }

        List<Comment> comments = commentRepository.findByNewsIdOrderByCmtIdAsc(newsId);

        List<String> memIds = comments.stream().map(Comment::getMemId).distinct().collect(Collectors.toList());
        java.util.Map<String, com.crewise.backend.domain.member.entity.Member> memberMap = memberRepository.findAllById(memIds)
                .stream().collect(Collectors.toMap(com.crewise.backend.domain.member.entity.Member::getMemId, m -> m));
                
        List<Long> imgIds = memberMap.values().stream().map(com.crewise.backend.domain.member.entity.Member::getUserImgId).filter(id -> id != null).distinct().collect(Collectors.toList());
        java.util.Map<Long, String> imgKeyMap = userImgRepository.findAllById(imgIds)
                .stream().collect(Collectors.toMap(com.crewise.backend.domain.user.entity.UserImg::getImgId, com.crewise.backend.domain.user.entity.UserImg::getImgFileKey));

        return comments.stream()
                .map(cmt -> {
                    com.crewise.backend.domain.member.entity.Member m = memberMap.get(cmt.getMemId());
                    String memNic = m != null ? m.getMemNic() : null;
                    String userImg = (m != null && m.getUserImgId() != null) ? imgKeyMap.get(m.getUserImgId()) : null;
                    return CommentResponse.from(cmt, memNic, userImg);
                })
                .collect(Collectors.toList());
    }

    // 댓글 작성 (팀 멤버만, M/A 타입만)
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, String userId) {
        News news = newsRepository.findById(request.getNewsId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소식입니다."));
        checkTeamMember(userId, news.getTeamId());

        if (!"M".equals(news.getTargetType()) && !"A".equals(news.getTargetType())) {
            throw new IllegalArgumentException("댓글을 달 수 없는 소식입니다.");
        }

        // memId 조회
        String memId = memberRepository.findByUserIdAndTeamId(userId, news.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."))
                .getMemId();

        Comment comment = Comment.builder()
                .cmtContent(request.getCmtContent())
                .cmtRegDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .newsId(request.getNewsId())
                .memId(memId)
                .build();

        return CommentResponse.from(commentRepository.save(comment));
    }

    // 댓글 수정 (본인만)
    @Transactional
    public CommentResponse updateComment(Long cmtId, String cmtContent, String userId) {
        Comment comment = commentRepository.findById(cmtId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        News news = newsRepository.findById(comment.getNewsId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소식입니다."));

        String memId = memberRepository.findByUserIdAndTeamId(userId, news.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."))
                .getMemId();

        if (!comment.getMemId().equals(memId)) {
            throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.update(cmtContent,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return CommentResponse.from(comment);
    }

    // 댓글 삭제 (본인만)
    @Transactional
    public void deleteComment(Long cmtId, String userId) {
        Comment comment = commentRepository.findById(cmtId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        News news = newsRepository.findById(comment.getNewsId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소식입니다."));

        String memId = memberRepository.findByUserIdAndTeamId(userId, news.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."))
                .getMemId();

        if (!comment.getMemId().equals(memId)) {
            throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}