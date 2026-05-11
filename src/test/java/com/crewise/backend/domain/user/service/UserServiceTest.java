package com.crewise.backend.domain.user.service;

import com.crewise.backend.domain.user.dto.SignupRequest;
import com.crewise.backend.domain.user.entity.User;
import com.crewise.backend.domain.user.repository.UserRepository;
import com.crewise.backend.global.config.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignupRequest request = SignupRequest.builder()
                .userEmail("test@test.com")
                .userPw("1234")
                .userName("테스트")
                .userTel("01012345678")
                .build();

        when(userRepository.existsByUserEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPw");
        when(userRepository.save(any())).thenReturn(mock(User.class));

        // when & then
        assertDoesNotThrow(() -> userService.signup(request));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("중복 이메일 회원가입 실패")
    void signup_duplicateEmail_fail() {
        // given
        SignupRequest request = SignupRequest.builder()
                .userEmail("test@test.com")
                .userPw("1234")
                .userName("테스트")
                .userTel("01012345678")
                .build();

        when(userRepository.existsByUserEmail(any())).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> userService.signup(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        com.crewise.backend.domain.user.dto.LoginRequest request = new com.crewise.backend.domain.user.dto.LoginRequest();

        User mockUser = mock(User.class);
        when(mockUser.getUserPw()).thenReturn("encodedPw");
        when(mockUser.getUserId()).thenReturn("testUserId");

        when(userRepository.findByUserEmail(any())).thenReturn(java.util.Optional.of(mockUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("testToken");

        // when & then
        assertDoesNotThrow(() -> userService.login(request));
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void deleteUser_success() {
        // given
        String userId = "testUserId";
        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));

        // when & then
        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    @DisplayName("존재하지 않는 회원 회원탈퇴 실패")
    void deleteUser_notFound_fail() {
        // given
        String userId = "testUserId";
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("이메일 중복 체크 성공")
    void checkEmailDuplicate_available() {
        // given
        String email = "test@test.com";
        when(userRepository.existsByUserEmail(any())).thenReturn(false);

        // when & then
        assertDoesNotThrow(() -> userService.checkEmail(email));
    }

    @Test
    @DisplayName("이메일 중복 체크 실패")
    void checkEmailDuplicate_duplicate() {
        // given
        String email = "test@test.com";
        when(userRepository.existsByUserEmail(any())).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.checkEmail(email));
    }

    @Test
    @DisplayName("회원정보 수정 성공")
    void updateUser_success() {
        // given
        String userId = "testUserId";
        com.crewise.backend.domain.user.dto.UserUpdateRequest request = com.crewise.backend.domain.user.dto.UserUpdateRequest.builder()
                .userPw("newPw")
                .userName("새이름")
                .userTel("01099998888")
                .build();

        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));
        when(passwordEncoder.encode("newPw")).thenReturn("encodedNewPw");

        // when & then
        assertDoesNotThrow(() -> userService.updateUser(userId, request));
        verify(mockUser, times(1)).update("encodedNewPw", "새이름", "01099998888");
    }
}