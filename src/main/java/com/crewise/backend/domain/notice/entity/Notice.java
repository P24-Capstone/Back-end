package com.crewise.backend.domain.notice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "NOTICES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTI_ID")
    private Long notiId;

    @Column(name = "NOTI_TITLE", length = 50, nullable = false)
    private String notiTitle;

    @Column(name = "NOTI_CONTENT")
    private String notiContent;

    @Column(name = "NOTI_FIX", length = 1)
    private String notiFix;

    @Column(name = "REG_DTM", length = 19)
    private String regDtm;

    @Column(name = "MOD_DTM", length = 19)
    private String modDtm;

    @Column(name = "TEAM_ID", length = 10, nullable = false)
    private String teamId;
}
