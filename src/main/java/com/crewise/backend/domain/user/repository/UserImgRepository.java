package com.crewise.backend.domain.user.repository;

import com.crewise.backend.domain.user.entity.UserImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserImgRepository extends JpaRepository<UserImg, Long> {
    List<UserImg> findByUserId(String userId);
}