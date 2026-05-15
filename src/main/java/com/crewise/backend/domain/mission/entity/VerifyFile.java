package com.crewise.backend.domain.mission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VERIFY_FILE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VerifyFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VERIFY_FILE_ID")
    private Long verifyFileId;

    @Column(name = "VERIFY_FILE_KEY", length = 1024, nullable = false)
    private String verifyFileKey;

    @Column(name = "VERIFY_ID", nullable = false)
    private Long verifyId;
}