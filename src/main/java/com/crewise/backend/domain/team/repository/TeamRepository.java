package com.crewise.backend.domain.team.repository;

import com.crewise.backend.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, String> {
    Optional<Team> findByCode(String code);
    List<Team> findAllByOrderByTeamIdDesc();

    List<Team> findByTeamNameContaining(String teamName);
}