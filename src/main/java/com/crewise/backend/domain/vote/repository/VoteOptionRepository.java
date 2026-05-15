package com.crewise.backend.domain.vote.repository;

import com.crewise.backend.domain.vote.entity.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    List<VoteOption> findByVoteId(Long voteId);

    void deleteByVoteId(Long voteId);
}