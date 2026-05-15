package com.crewise.backend.domain.news.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "COMMENTS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CMT_ID")
    private Long cmtId;

    @Column(name = "CMT_CONTENT", nullable = false)
    private String cmtContent;

    @Column(name = "CMT_REG_DTM", length = 19, nullable = false)
    private String cmtRegDtm;

    @Column(name = "CMT_MOD_DTM", length = 19)
    private String cmtModDtm;

    @Column(name = "NEWS_ID", nullable = false)
    private Long newsId;

    @Column(name = "MEM_ID", length = 26, nullable = false)
    private String memId;

    public void update(String cmtContent, String cmtModDtm) {
        this.cmtContent = cmtContent;
        this.cmtModDtm = cmtModDtm;
    }
}