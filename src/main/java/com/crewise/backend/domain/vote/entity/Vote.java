package com.crewise.backend.domain.vote.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VOTE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VOTE_ID")
    private Long voteId;

    @Column(name = "VOTE_TITLE", length = 50, nullable = false)
    private String voteTitle;

    @Column(name = "VOTE_CONTENT", nullable = false)
    private String voteContent;

    @Column(name = "VOTE_START_DT", length = 10, nullable = false)
    private String voteStartDt;

    @Column(name = "VOTE_END_DT", length = 10, nullable = false)
    private String voteEndDt;

    @Column(name = "VOTE_TYPE", length = 1, nullable = false)
    private String voteType;

    @Column(name = "VOTE_RULE", length = 1, nullable = false)
    private String voteRule;

    @Column(name = "VOTE_MULTI", length = 1, nullable = false)
    private String voteMulti;

    @Column(name = "VOTE_REG_DTM", length = 19, nullable = false)
    private String voteRegDtm;

    @Column(name = "TEAM_ID", length = 10, nullable = false)
    private String teamId;
}