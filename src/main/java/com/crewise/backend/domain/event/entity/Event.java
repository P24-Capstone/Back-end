package com.crewise.backend.domain.event.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EVENTS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVT_ID")
    private Long evtId;

    @Column(name = "EVT_TITLE", length = 50, nullable = false)
    private String evtTitle;

    @Column(name = "EVT_CONTENT", nullable = false)
    private String evtContent;

    @Column(name = "EVT_START_DT", length = 10, nullable = false)
    private String evtStartDt;

    @Column(name = "EVT_END_DT", length = 10, nullable = false)
    private String evtEndDt;

    @Column(name = "EVT_REG_DTM", length = 19, nullable = false)
    private String evtRegDtm;

    @Column(name = "TEAM_ID", length = 10, nullable = false)
    private String teamId;
}