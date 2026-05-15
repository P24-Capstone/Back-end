package com.crewise.backend.domain.news.repository;

import com.crewise.backend.domain.news.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNewsIdOrderByCmtIdAsc(Long newsId);
}