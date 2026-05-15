package com.crewise.backend.domain.meetingrecord.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "REC_FILE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REC_FILE_ID")
    private Long recFileId;

    @Column(name = "MEETING_ID")
    private Long meetingId;

    @Column(name = "REC_FILE_KEY", length = 1024, nullable = false)
    private String recFileKey;
}