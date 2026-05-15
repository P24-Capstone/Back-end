package com.crewise.backend.domain.vote.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VoteHistoryId implements Serializable {
    private Long voteId;
    private String memId;
}