package com.crewise.backend.domain.news.repository;

import com.crewise.backend.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByTeamIdOrderByNewsIdDesc(String teamId);
    List<News> findByTeamIdInOrderByNewsIdDesc(List<String> teamIds);
}
