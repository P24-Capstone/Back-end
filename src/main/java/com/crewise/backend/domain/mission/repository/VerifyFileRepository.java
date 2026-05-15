package com.crewise.backend.domain.mission.repository;

import com.crewise.backend.domain.mission.entity.VerifyFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerifyFileRepository extends JpaRepository<VerifyFile, Long> {
    List<VerifyFile> findByVerifyId(Long verifyId);
}