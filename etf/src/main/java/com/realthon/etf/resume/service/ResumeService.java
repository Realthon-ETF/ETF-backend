package com.realthon.etf.resume.service;

import com.realthon.etf.ai.OpenAiClient;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.resume.PdfTextExtractor;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.domain.UserResumeSummary;
import com.realthon.etf.user.dto.response.UserResumeSummaryResponse;
import com.realthon.etf.user.repository.UserRepository;
import com.realthon.etf.user.repository.UserResumeSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final UserRepository userRepository;
    private final UserResumeSummaryRepository resumeSummaryRepository;
    private final PdfTextExtractor pdfTextExtractor;
    private final OpenAiClient openAiClient;

    @Transactional
    public UserResumeSummaryResponse uploadAndSummarize(String loginId, MultipartFile file) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // PDF → 텍스트
        String plainText = pdfTextExtractor.extractText(file);

        // GPT 요약
        String summary = openAiClient.summarizeResume(plainText);

        // 4) user_resume_summary upsert
        UserResumeSummary entity = resumeSummaryRepository.findByUser(user)
                .orElseGet(() -> UserResumeSummary.builder()
                        .user(user)
                        .summary(summary)
                        .build()
                );

        if (entity.getId() != null) {
            entity.updateSummary(summary);
        } else {
            resumeSummaryRepository.save(entity);
        }

        return UserResumeSummaryResponse.from(entity);
    }

    @Transactional(readOnly = true)
    public Optional<UserResumeSummaryResponse> getMyResumeSummary(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return resumeSummaryRepository.findByUser(user)
                .map(UserResumeSummaryResponse::from);
    }
}
