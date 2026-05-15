package com.crewise.backend.domain.vote.repository;

import com.crewise.backend.domain.vote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByTeamIdOrderByVoteIdDesc(String teamId);
}