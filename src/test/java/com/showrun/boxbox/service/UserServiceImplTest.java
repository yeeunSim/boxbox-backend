package com.showrun.boxbox.service;

import com.showrun.boxbox.exception.BoxboxException;
import com.showrun.boxbox.exception.ErrorCode;
import com.showrun.boxbox.repository.LoginRepository;
import com.showrun.boxbox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks private UserServiceImpl userServiceImpl;
    @Mock private UserRepository userRepository;
    @Mock private LoginRepository loginRepository;
    private final String VALID_NICKNAME = "TestUser10";
    private final String INVALID_NICKNAME_LONG = "VeryLongNickname"; // 10자 초과
    private final String INVALID_NICKNAME_CHAR = "Test@User"; // 특수 문자 포함
    private final String VALID_EMAIL = "test@example.com";



    @Test
    @DisplayName("ensureNicknameAvailable_성공_사용가능한_닉네임")
    void ensureNicknameAvailable_Success() {

        // given
        when(userRepository.existsByUserNickname(VALID_NICKNAME)).thenReturn(false);

        // when
        boolean result = userServiceImpl.ensureNicknameAvailable(VALID_NICKNAME);

        // then
        assertTrue(result);
        verify(userRepository, times(1)).existsByUserNickname(VALID_NICKNAME);
    }

    @Test
    @DisplayName("ensureNicknameAvailable_실패_중복된_닉네임")
    void ensureNicknameAvailable_Failure_DuplicateNickname() {

        // given
        when(userRepository.existsByUserNickname(VALID_NICKNAME)).thenReturn(true);

        // when
        BoxboxException exception = assertThrows(BoxboxException.class, () -> {
            userServiceImpl.ensureNicknameAvailable(VALID_NICKNAME);
        });

        // then
        assertEquals(ErrorCode.DUPLICATE_NICKNAME, exception.getErrorCode());
        verify(userRepository, times(1)).existsByUserNickname(VALID_NICKNAME);
    }

    @Test
    @DisplayName("ensureNicknameAvailable_실패_닉네임_Null")
    void ensureNicknameAvailable_Failure_NicknameNull() {

        // when
        BoxboxException exception = assertThrows(BoxboxException.class, () -> {
            userServiceImpl.ensureNicknameAvailable(null);
        });

        // then
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        verify(userRepository, never()).existsByUserNickname(anyString());
    }

    @Test
    @DisplayName("ensureNicknameAvailable_실패_유효하지않은_닉네임_길이")
    void ensureNicknameAvailable_Failure_NicknameInvalidLength() {

        // when
        BoxboxException exception = assertThrows(BoxboxException.class, () -> {
            userServiceImpl.ensureNicknameAvailable(INVALID_NICKNAME_LONG);
        });

        // then
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        verify(userRepository, never()).existsByUserNickname(anyString());
    }

    @Test
    @DisplayName("ensureNicknameAvailable_실패_유효하지않은_닉네임_형식")
    void ensureNicknameAvailable_Failure_NicknameInvalidCharacters() {

        // when
        BoxboxException exception = assertThrows(BoxboxException.class, () -> {
            userServiceImpl.ensureNicknameAvailable(INVALID_NICKNAME_CHAR);
        });

        // then
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        verify(userRepository, never()).existsByUserNickname(anyString());
    }


    @Test
    @DisplayName("ensureEmailAvailable_성공_사용가능한_이메일")
    void ensureEmailAvailable_Success() {

        // given
        when(loginRepository.existsByLoginEmail(VALID_EMAIL)).thenReturn(false);

        // when
        boolean result = userServiceImpl.ensureEmailAvailable(VALID_EMAIL);

        // then
        assertTrue(result);
        verify(loginRepository, times(1)).existsByLoginEmail(VALID_EMAIL);
    }

    @Test
    @DisplayName("ensureEmailAvailable_실패_중복된_이메일")
    void ensureEmailAvailable_Failure_DuplicateEmail() {

        // given
        when(loginRepository.existsByLoginEmail(VALID_EMAIL)).thenReturn(true);

        // when
        BoxboxException exception = assertThrows(BoxboxException.class, () -> {
            userServiceImpl.ensureEmailAvailable(VALID_EMAIL);
        });

        // then
        assertEquals(ErrorCode.DUPLICATE_EMAIL, exception.getErrorCode());
        verify(loginRepository, times(1)).existsByLoginEmail(VALID_EMAIL);
    }

    @Test
    @DisplayName("ensureEmailAvailable_실패_이메일_Null")
    void ensureEmailAvailable_Failure_EmailNull() {

        // when
        BoxboxException exception = assertThrows(BoxboxException.class, () -> {
            userServiceImpl.ensureEmailAvailable(null);
        });

        // then
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        verify(loginRepository, never()).existsByLoginEmail(anyString());
    }

    @Test
    @DisplayName("ensureEmailAvailable_실패_이메일_Empty")
    void ensureEmailAvailable_Failure_EmailEmpty() {

        // when
        BoxboxException exception = assertThrows(BoxboxException.class, () -> {
            userServiceImpl.ensureEmailAvailable("");
        });

        // then
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        verify(loginRepository, never()).existsByLoginEmail(anyString());
    }
}
