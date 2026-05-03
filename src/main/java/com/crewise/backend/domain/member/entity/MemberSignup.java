package com.crewise.backend.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_SIGNUP")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberSignup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SIGNUP_ID")
    private Long signupId;

    @Column(name = "TEAM_ID", length = 10, nullable = false)
    private String teamId;

    @Column(name = "USER_ID", length = 26, nullable = false)
    private String userId;

    @Column(name = "SIGN_STATE", length = 1, nullable = false)
    private String signState;

    @Column(name = "REG_DTM", length = 19, nullable = false)
    private String regDtm;

    @Column(name = "PROC_DTM", length = 19)
    private String procDtm;
}