package com.crewise.backend.domain.vote.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VOTE_OPTION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPT_SN")
    private Long optSn;

    @Column(name = "OPT_CONTENT", length = 100, nullable = false)
    private String optContent;

    @Column(name = "VOTE_ID", nullable = false)
    private Long voteId;
}