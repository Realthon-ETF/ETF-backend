package com.realthon.etf.email.repository;

import com.realthon.etf.email.domain.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

    // 가장 최근 코드
    Optional<EmailVerificationCode> findTopByEmailOrderByIdDesc(String email);

    // 특정 코드
    Optional<EmailVerificationCode> findByEmailAndCode(String email, String code);
}

