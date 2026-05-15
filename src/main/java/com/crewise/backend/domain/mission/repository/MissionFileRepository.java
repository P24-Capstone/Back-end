package com.crewise.backend.domain.mission.repository;

import com.crewise.backend.domain.mission.entity.MissionFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionFileRepository extends JpaRepository<MissionFile, Long> {
    List<MissionFile> findByMissionId(Long missionId);
}