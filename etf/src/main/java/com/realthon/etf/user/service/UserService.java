package com.realthon.etf.user.service;

import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.dto.request.CreateUserRequest;
import com.realthon.etf.user.dto.request.UpdateUserRequest;
import com.realthon.etf.user.dto.response.UserResponse;
import com.realthon.etf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
    회원가입
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

//        // 중복 체크
//        if (userRepository.existsByLoginId(request.getLoginId())) {
//            throw new CustomException(ExceptionCode.DUPLICATE_LOGIN_ID);
//        }
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new CustomException(ExceptionCode.DUPLICATE_EMAIL);
//        }
//        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
//            throw new CustomException(ExceptionCode.DUPLICATE_PHONE_NUMBER);
//        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = request.toEntity(encodedPassword);

        return UserResponse.from(userRepository.save(user));

    }

    /*
    내 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserResponse getMyProfileByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    /*
    내 프로필 수정
     */
    @Transactional
    public UserResponse updateMyProfile(String loginId, UpdateUserRequest request) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        user.updateProfile(
                request.getUsername(),
                request.getPhoneNumber(),
                request.getEmail(),
                request.getSchool(),
                request.getMajor(),
                request.getInterestFields(),
                request.getIntervalDays(),
                request.getAlarmTime()
        );

        return UserResponse.from(user);
    }

    /*
    회원탈퇴
     */
    // user 삭제
    @Transactional
    public void deleteUser(String loginId, String password) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ExceptionCode.INVALID_PASSWORD);
        }

        userRepository.delete(user);
    }

    /*
    로그인 ID, 전화번호 중복 체크
     */
    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    public boolean isPhoneAvailable(String phoneNumber) {
        String normalized = normalizePhone(phoneNumber);
        return !userRepository.existsByPhoneNumber(normalized);
    }

    private String normalizePhone(String phoneNumber) {
        if (phoneNumber == null) return null;
        return phoneNumber.replaceAll("[^0-9]", "");
    }

}
