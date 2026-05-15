package com.crewise.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_IMG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMG_ID")
    private Long imgId;

    @Column(name = "USER_ID", length = 26, nullable = false)
    private String userId;

    @Column(name = "IMG_FILE_KEY", length = 1024)
    private String imgFileKey;
}