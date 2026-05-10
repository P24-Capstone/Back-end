package com.crewise.backend.domain.member.repository;

import com.crewise.backend.domain.member.entity.MemberSignup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberSignupRepository extends JpaRepository<MemberSignup, Long> {
    List<MemberSignup> findByTeamIdOrderByRegDtmDesc(String teamId);

    Optional<MemberSignup> findByUserIdAndTeamId(String userId, String teamId);

    boolean existsByUserIdAndTeamIdAndSignState(String userId, String teamId, String signState);
}