package com.realthon.etf.user.service;

import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.dto.request.CreateUserRequest;
import com.realthon.etf.user.dto.response.CreateUserResponse;
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
    public CreateUserResponse createUser(CreateUserRequest request) {

        // 중복 체크
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity(encodedPassword);

        return CreateUserResponse.from(userRepository.save(user));

    }

}
