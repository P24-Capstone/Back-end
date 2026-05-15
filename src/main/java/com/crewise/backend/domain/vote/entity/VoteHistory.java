package com.crewise.backend.domain.vote.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VOTE_HISTORY")
@IdClass(VoteHistoryId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VoteHistory {

    @Id
    @Column(name = "VOTE_ID")
    private Long voteId;

    @Id
    @Column(name = "MEM_ID", length = 26)
    private String memId;

    @Column(name = "OPT_SN", nullable = false)
    private Long optSn;
}