package com.realthon.etf.email.service;

import com.realthon.etf.auth.jwt.JwtUtil;
import com.realthon.etf.email.domain.EmailVerificationCode;
import com.realthon.etf.email.repository.EmailVerificationCodeRepository;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationCodeRepository codeRepository;
    private final EmailSender emailSender;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 인증번호 유효시간 (밀리초)
    @Value("${mail.auth-code-expiration-millis:300000}") // 기본 5분
    private long authCodeExpirationMillis;

    // resetToken 만료시간 (초) - 응답용
    @Value("${spring.jwt.reset-ttl:600000}") // 기본 10분(밀리초)
    private long resetTtlMillis;

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(900000) + 100000;
        return String.valueOf(num);
    }

    private LocalDateTime calcExpiresAt() {
        long seconds = Math.max(60, authCodeExpirationMillis / 1000); // 최소 60초
        return LocalDateTime.now().plusSeconds(seconds);
    }

    // 인증번호 발송
    @Transactional
    public void sendCodeIfUserExists(String username, String phoneNumber, String email) {

        boolean exists = userRepository.existsByUsernameAndPhoneNumberAndEmail(username, phoneNumber, email);
        if (!exists) {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }

        String code = generateCode();

        EmailVerificationCode entity =
                new EmailVerificationCode(email, code, calcExpiresAt());
        codeRepository.save(entity);

        String subject = "[알려주잡] 비밀번호 재설정 인증번호 안내";
        String content = "안녕하세요, 알려주잡입니다.\n\n" +
                "요청하신 비밀번호 재설정 인증번호는 [" + code + "] 입니다.\n" +
                "보안을 위해 제한 시간 내에 입력해 주세요.\n\n" +
                "감사합니다.";

        emailSender.send(email, subject, content);
    }

    // 인증번호 검증 + resetToken(JWT) 발급
    @Transactional
    public String verifyCodeAndIssueResetToken(String email, String code) {

        EmailVerificationCode entity = codeRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new CustomException(ExceptionCode.EMAIL_VERIFICATION_CODE_INVALID));

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ExceptionCode.EMAIL_VERIFICATION_CODE_EXPIRED);
        }

        entity.markVerified();

        // resetToken 발급 (subject=email)
        return jwtUtil.createPasswordResetToken(email);
    }

    //resetToken 만료 시간(초) - 응답에 넣고 싶을 때 사용
    public long getResetExpiresInSec() {
        return resetTtlMillis / 1000;
    }
}