package com.crewise.backend.domain.team.repository;

import com.crewise.backend.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, String> {
    List<Team> findAllByOrderByTeamIdDesc();

    List<Team> findByTeamNameContaining(String teamName);
}