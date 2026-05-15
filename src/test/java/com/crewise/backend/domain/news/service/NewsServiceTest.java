package com.crewise.backend.domain.news.service;

import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.news.dto.CommentCreateRequest;
import com.crewise.backend.domain.news.entity.Comment;
import com.crewise.backend.domain.news.entity.News;
import com.crewise.backend.domain.news.repository.CommentRepository;
import com.crewise.backend.domain.news.repository.NewsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @InjectMocks
    private NewsService newsService;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("최근 소식 목록 조회 성공 - 팀 멤버")
    void getNewsList_success() {
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(true);
        when(newsRepository.findByTeamIdOrderByNewsIdDesc(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> newsService.getNewsList("30MNKA", "userId"));
    }

    @Test
    @DisplayName("최근 소식 목록 조회 실패 - 비멤버")
    void getNewsList_fail_notMember() {
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> newsService.getNewsList("30MNKA", "userId"));
    }

    @Test
    @DisplayName("댓글 작성 성공 - M 타입")
    void createComment_success_typeM() {
        CommentCreateRequest request = CommentCreateRequest.builder()
                .newsId(1L)
                .cmtContent("환영합니다!")
                .build();

        News mockNews = mock(News.class);
        when(mockNews.getTargetType()).thenReturn("M");
        when(mockNews.getTeamId()).thenReturn("30MNKA");
        when(newsRepository.findById(any())).thenReturn(Optional.of(mockNews));

        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(true);

        Member mockMember = mock(Member.class);
        when(mockMember.getMemId()).thenReturn("memId");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        when(commentRepository.save(any())).thenReturn(mock(Comment.class));

        assertDoesNotThrow(() -> newsService.createComment(request, "userId"));
    }

    @Test
    @DisplayName("댓글 작성 실패 - 댓글 불가 타입")
    void createComment_fail_invalidType() {
        CommentCreateRequest request = CommentCreateRequest.builder()
                .newsId(1L)
                .cmtContent("댓글")
                .build();

        News mockNews = mock(News.class);
        when(mockNews.getTargetType()).thenReturn("N");
        when(mockNews.getTeamId()).thenReturn("30MNKA");
        when(newsRepository.findById(any())).thenReturn(Optional.of(mockNews));

        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> newsService.createComment(request, "userId"));
    }

    @Test
    @DisplayName("댓글 수정 성공 - 본인")
    void updateComment_success() {
        Comment mockComment = mock(Comment.class);
        when(mockComment.getNewsId()).thenReturn(1L);
        when(mockComment.getMemId()).thenReturn("memId");
        when(commentRepository.findById(any())).thenReturn(Optional.of(mockComment));

        News mockNews = mock(News.class);
        when(mockNews.getTeamId()).thenReturn("30MNKA");
        when(newsRepository.findById(any())).thenReturn(Optional.of(mockNews));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemId()).thenReturn("memId");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertDoesNotThrow(() -> newsService.updateComment(1L, "수정된 댓글", "userId"));
        verify(mockComment, times(1)).update(any(), any());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 타인")
    void updateComment_fail_notOwner() {
        Comment mockComment = mock(Comment.class);
        when(mockComment.getNewsId()).thenReturn(1L);
        when(mockComment.getMemId()).thenReturn("otherMemId");
        when(commentRepository.findById(any())).thenReturn(Optional.of(mockComment));

        News mockNews = mock(News.class);
        when(mockNews.getTeamId()).thenReturn("30MNKA");
        when(newsRepository.findById(any())).thenReturn(Optional.of(mockNews));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemId()).thenReturn("memId");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> newsService.updateComment(1L, "수정된 댓글", "userId"));
    }

    @Test
    @DisplayName("댓글 삭제 성공 - 본인")
    void deleteComment_success() {
        Comment mockComment = mock(Comment.class);
        when(mockComment.getNewsId()).thenReturn(1L);
        when(mockComment.getMemId()).thenReturn("memId");
        when(commentRepository.findById(any())).thenReturn(Optional.of(mockComment));

        News mockNews = mock(News.class);
        when(mockNews.getTeamId()).thenReturn("30MNKA");
        when(newsRepository.findById(any())).thenReturn(Optional.of(mockNews));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemId()).thenReturn("memId");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertDoesNotThrow(() -> newsService.deleteComment(1L, "userId"));
        verify(commentRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 타인")
    void deleteComment_fail_notOwner() {
        Comment mockComment = mock(Comment.class);
        when(mockComment.getNewsId()).thenReturn(1L);
        when(mockComment.getMemId()).thenReturn("otherMemId");
        when(commentRepository.findById(any())).thenReturn(Optional.of(mockComment));

        News mockNews = mock(News.class);
        when(mockNews.getTeamId()).thenReturn("30MNKA");
        when(newsRepository.findById(any())).thenReturn(Optional.of(mockNews));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemId()).thenReturn("memId");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> newsService.deleteComment(1L, "userId"));
        verify(commentRepository, never()).delete(any());
    }
}