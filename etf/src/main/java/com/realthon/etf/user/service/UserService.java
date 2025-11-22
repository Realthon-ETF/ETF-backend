package com.realthon.etf.user.service;

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

    /**
     * 회원가입
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        // 중복 체크
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

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
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    /*
    프로필 수정
     */
    @Transactional
    public UserResponse updateMyProfile(String loginId, UpdateUserRequest request) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.updateProfile(
                request.getUsername(),
                request.getPhoneNumber(),
                request.getEmail(),
                request.getSchool(),
                request.getMajor(),
                request.getInterestField(),
                request.getIntervalDays(),
                request.getAlarmTime()
        );

        return UserResponse.from(user);
    }

}
