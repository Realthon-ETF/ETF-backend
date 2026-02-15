package com.realthon.etf.email.service;

import com.realthon.etf.email.domain.EmailVerificationCode;
import com.realthon.etf.email.repository.EmailVerificationCodeRepository;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationCodeRepository codeRepository;
    private final EmailSender emailSender;
    private final UserRepository userRepository;

    private String generateCode() {
        Random random = new Random();
        int num = random.nextInt(900000) + 100000;
        return String.valueOf(num);
    }

    // 인증번호 발송 (기존 사용자 검증 포함)
    @Transactional
    public void sendCode(String name, String phoneNumber, String email) {

        boolean exists = userRepository.existsByUsernameAndPhoneNumberAndEmail(name, phoneNumber, email);
        if (!exists) {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }

        String code = generateCode();

        EmailVerificationCode entity =
                new EmailVerificationCode(email, code, LocalDateTime.now().plusMinutes(5));

        codeRepository.save(entity);

        String subject = "[알려주잡] 비밀번호 재설정 인증번호 안내";
        String content = "안녕하세요, 알려주잡입니다.\n\n" +
                "요청하신 비밀번호 재설정 인증번호는 [" + code + "] 입니다.\n" +
                "보안을 위해 5분 이내에 입력해 주세요.\n\n" +
                "감사합니다.";

        emailSender.send(email, subject, content);
    }

    // 인증번호 확인
    @Transactional
    public void verifyCode(String email, String code) {
        EmailVerificationCode entity = codeRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new CustomException(ExceptionCode.EMAIL_VERIFICATION_CODE_INVALID));

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ExceptionCode.EMAIL_VERIFICATION_CODE_EXPIRED);
        }

        entity.markVerified();
    }

    // 인증 완료된 상태인지 확인
    @Transactional(readOnly = true)
    public void ensureVerified(String email) {
        EmailVerificationCode entity = codeRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.EMAIL_VERIFICATION_REQUIRED));

        if (!entity.isVerified()) {
            throw new CustomException(ExceptionCode.EMAIL_VERIFICATION_REQUIRED);
        }
    }
}