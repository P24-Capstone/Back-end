package com.crewise.backend.domain.user.service;

import com.crewise.backend.domain.user.dto.FindEmailRequest;
import com.crewise.backend.domain.user.dto.FindEmailResponse;
import com.crewise.backend.domain.user.dto.LoginRequest;
import com.crewise.backend.domain.user.dto.ResetPasswordRequest;
import com.crewise.backend.domain.user.dto.SignupRequest;
import com.crewise.backend.domain.user.dto.UserResponse;
import com.crewise.backend.domain.user.dto.UserUpdateRequest;
import com.crewise.backend.domain.user.dto.VerifyUserRequest;
import com.crewise.backend.domain.user.entity.User;
import com.crewise.backend.domain.user.entity.UserImg;
import com.crewise.backend.domain.user.repository.UserImgRepository;
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
    private final UserImgRepository userImgRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public static final String DEFAULT_IMG_KEY = "default";

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

        // 가입 시 기본 이미지 레코드 자동 생성 (MEMBER.USER_IMG_ID NOT NULL 제약 충족)
        userImgRepository.save(UserImg.builder()
                .userId(user.getUserId())
                .imgFileKey(DEFAULT_IMG_KEY)
                .build());
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

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserResponse getUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return UserResponse.from(user);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        userRepository.delete(user);
    }

    // 회원정보 수정
    @Transactional
    public void updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        String encodedPw = (request.getUserPw() != null && !request.getUserPw().isEmpty())
                ? passwordEncoder.encode(request.getUserPw())
                : null;

        user.update(encodedPw, request.getUserName(), request.getUserTel());
    }

    // 이메일 중복 확인
    public void checkEmail(String email) {
        if (userRepository.existsByUserEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    // 이메일 찾기 (이름 + 전화번호)
    @Transactional(readOnly = true)
    public FindEmailResponse findEmail(FindEmailRequest request) {
        User user = userRepository.findByUserNameAndUserTel(request.getName(), request.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 계정을 찾을 수 없습니다."));
        return new FindEmailResponse(user.getUserEmail());
    }

    // 본인 확인 (이메일 + 전화번호)
    @Transactional(readOnly = true)
    public void verifyUser(VerifyUserRequest request) {
        userRepository.findByUserEmailAndUserTel(request.getEmail(), request.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 계정을 찾을 수 없습니다."));
    }

    // 비밀번호 재설정 (이메일 + 전화번호 + 새 비밀번호)
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByUserEmailAndUserTel(request.getEmail(), request.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 계정을 찾을 수 없습니다."));
        user.update(passwordEncoder.encode(request.getNewPassword()), null, null);
    }

    // ULID 대신 임시로 UUID 사용
    private String generateUlid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 26);
    }
}