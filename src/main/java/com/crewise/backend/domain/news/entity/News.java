package com.crewise.backend.domain.news.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "NEWS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NEWS_ID")
    private Long newsId;

    @Column(name = "TARGET_TYPE", length = 1, nullable = false)
    private String targetType;

    @Column(name = "TARGET_ID")
    private Long targetId;

    @Column(name = "NEWS_CONTENT", length = 255, nullable = false)
    private String newsContent;

    @Column(name = "TEAM_ID", length = 10, nullable = false)
    private String teamId;
}