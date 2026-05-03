package com.crewise.backend.domain.member.repository;

import com.crewise.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    List<Member> findByTeamIdOrderByMemRoleAsc(String teamId);

    Optional<Member> findByUserIdAndTeamId(String userId, String teamId);

    boolean existsByUserIdAndTeamId(String userId, String teamId);
}