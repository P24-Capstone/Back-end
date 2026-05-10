package com.crewise.backend.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @Column(name = "MEM_ID", length = 26)
    private String memId;

    @Column(name = "MEM_NIC", length = 20, nullable = false)
    private String memNic;

    @Column(name = "MEM_ROLE", length = 1, nullable = false)
    private String memRole;

    @Column(name = "MEM_STATE", length = 1, nullable = false)
    private String memState;

    @Column(name = "REG_DTM", length = 19, nullable = false)
    private String regDtm;

    @Column(name = "PROC_DTM", length = 19)
    private String procDtm;

    @Column(name = "USER_ID", length = 26, nullable = false)
    private String userId;

    @Column(name = "TEAM_ID", length = 10, nullable = false)
    private String teamId;
}