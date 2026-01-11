package com.realthon.etf.resume.controller;

import com.realthon.etf.auth.dto.CustomUserDetails;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.resume.dto.request.UpdateUserResumeSummaryRequest;
import com.realthon.etf.resume.service.ResumeService;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.resume.dto.response.UserResumeSummaryResponse;
import com.realthon.etf.user.repository.UserRepository;
import com.realthon.etf.resume.repository.UserResumeSummaryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ResumeController {

    private final UserRepository userRepository;
    private final UserResumeSummaryRepository resumeSummaryRepository;
    private final ResumeService resumeService;

    /*
    이력서 pdf 업로드
     */
    // 아래는 이미 구현해둔 PDF 업로드 + 요약/저장용 API
    @PostMapping("/resumes/pdf")
    public ResponseEntity<UserResumeSummaryResponse> uploadResumePdf(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                     @RequestPart("file") MultipartFile file) {
        String loginId = userDetails.getUsername();
        if (file == null || file.isEmpty()) {
            throw new CustomException(ExceptionCode.RESUME_FILE_REQUIRED);
        }

        UserResumeSummaryResponse response = resumeService.uploadAndSummarize(loginId, file);
        return ResponseEntity.ok(response);
    }

    /*
    이력서 요약본 조회
     */
    @GetMapping("/auth/resume")
    public ResponseEntity<UserResumeSummaryResponse> getMyResumeSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String loginId = userDetails.getUsername();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return resumeSummaryRepository.findByUser(user)
                .map(UserResumeSummaryResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
    이력서 요약본 수정
     */
    @PatchMapping("/auth/resume")
    public ResponseEntity<UserResumeSummaryResponse> updateResumeSummary(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @Valid @RequestBody UpdateUserResumeSummaryRequest request) {
        String loginId = userDetails.getUsername();
        UserResumeSummaryResponse response = resumeService.updateUserResumeSummaryResponse(loginId, request);
        return ResponseEntity.ok(response);
    }

}

