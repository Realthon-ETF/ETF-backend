package com.realthon.etf.resume.controller;

import com.realthon.etf.resume.service.ResumeService;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.dto.response.UserResumeSummaryResponse;
import com.realthon.etf.user.repository.UserRepository;
import com.realthon.etf.user.repository.UserResumeSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resumes/")
public class ResumeController {

    private final UserRepository userRepository;
    private final UserResumeSummaryRepository resumeSummaryRepository;
    private final ResumeService resumeService;

    @GetMapping("/pdf")
    public ResponseEntity<UserResumeSummaryResponse> getMyResumeSummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return resumeSummaryRepository.findByUser(user)
                .map(UserResumeSummaryResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 아래는 이미 구현해둔 PDF 업로드 + 요약/저장용 API
    @PostMapping("/pdf")
    public ResponseEntity<UserResumeSummaryResponse> uploadResumePdf(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("file") MultipartFile file
    ) {
        String loginId = userDetails.getUsername();
        UserResumeSummaryResponse response = resumeService.uploadAndSummarize(loginId, file);
        return ResponseEntity.ok(response);
    }


}

