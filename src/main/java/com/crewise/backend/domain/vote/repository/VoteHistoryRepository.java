package com.crewise.backend.domain.vote.repository;

import com.crewise.backend.domain.vote.entity.VoteHistory;
import com.crewise.backend.domain.vote.entity.VoteHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteHistoryRepository extends JpaRepository<VoteHistory, VoteHistoryId> {
    List<VoteHistory> findByVoteId(Long voteId);

    boolean existsByVoteIdAndMemId(Long voteId, String memId);

    Optional<VoteHistory> findByVoteIdAndMemId(Long voteId, String memId);

    void deleteByVoteIdAndMemId(Long voteId, String memId);
}