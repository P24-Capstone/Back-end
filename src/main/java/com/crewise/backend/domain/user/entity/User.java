package com.crewise.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "USER_ID", length = 26)
    private String userId;

    @Column(name = "USER_EMAIL", length = 100, nullable = false, unique = true)
    private String userEmail;

    @Column(name = "USER_PW", length = 64, nullable = false)
    private String userPw;

    @Column(name = "USER_NAME", length = 50, nullable = false)
    private String userName;

    @Column(name = "USER_TEL", length = 11, nullable = false)
    private String userTel;
}