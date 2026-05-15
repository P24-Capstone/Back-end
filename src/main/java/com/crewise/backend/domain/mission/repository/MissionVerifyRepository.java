package com.crewise.backend.domain.mission.repository;

import com.crewise.backend.domain.mission.entity.MissionVerify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionVerifyRepository extends JpaRepository<MissionVerify, Long> {
    List<MissionVerify> findByMissionId(Long missionId);

    List<MissionVerify> findByMemId(String memId);
}