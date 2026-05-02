package com.crewise.backend.domain.user.service;

import com.crewise.backend.domain.user.dto.LoginRequest;
import com.crewise.backend.domain.user.dto.SignupRequest;
import com.crewise.backend.domain.user.entity.User;
import com.crewise.backend.domain.user.repository.UserRepository;
import com.crewise.backend.global.config.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByUserEmail(request.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        User user = User.builder()
                .userId(generateUlid())
                .userEmail(request.getUserEmail())
                .userPw(passwordEncoder.encode(request.getUserPw()))
                .userName(request.getUserName())
                .userTel(request.getUserTel())
                .build();

        userRepository.save(user);
    }

    // 로그인
    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        User user = userRepository.findByUserEmail(request.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getUserPw(), user.getUserPw())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return jwtUtil.generateToken(user.getUserId());
    }

    // ULID 대신 임시로 UUID 사용
    private String generateUlid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 26);
    }

    // 이메일 중복 확인
    public void checkEmail(String email) {
        if (userRepository.existsByUserEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }
}