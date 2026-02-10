package com.realthon.etf.targetUrl.controller;

import com.realthon.etf.targetUrl.dto.request.TargetUrlRequest;
import com.realthon.etf.targetUrl.dto.response.TargetUrlListResponse;
import com.realthon.etf.targetUrl.dto.response.TargetUrlResponse;
import com.realthon.etf.targetUrl.service.TargetUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/target-urls")
public class TargetUrlController {

    private final TargetUrlService targetUrlService;

    /*
    target-url 등록
    POST /users/me/target-urls
     */
    @PostMapping
    public ResponseEntity<TargetUrlResponse> create(@AuthenticationPrincipal UserDetails userDetails,
                                                    @Valid @RequestBody TargetUrlRequest request) {
        return ResponseEntity.ok(targetUrlService.create(userDetails.getUsername(), request));
    }

    /*
    target-url 수정
    PATCH /users/me/target-urls/{targetUrlId}
     */
    @PatchMapping("/{targetUrlId}")
    public ResponseEntity<TargetUrlResponse> update(@AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable Long targetUrlId,
                                                    @Valid @RequestBody TargetUrlRequest request) {
        return ResponseEntity.ok(targetUrlService.update(userDetails.getUsername(), targetUrlId, request));
    }

    /*
    target-url 삭제
    DELETE /users/me/target-urls/{targetUrlId}
     */
    @DeleteMapping("/{targetUrlId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails userDetails,
                                       @PathVariable Long targetUrlId) {
        targetUrlService.delete(userDetails.getUsername(), targetUrlId);
        return ResponseEntity.noContent().build();
    }

    /*
    target-url 리스트 조회
    GET /users/me/target-urls
     */
    @GetMapping
    public ResponseEntity<TargetUrlListResponse> list(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(targetUrlService.list(userDetails.getUsername()));
    }
}
