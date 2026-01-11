package com.realthon.etf.notification.controller;

import com.realthon.etf.auth.dto.CustomUserDetails;
import com.realthon.etf.notification.dto.response.NotificationLikeResponse;
import com.realthon.etf.notification.dto.response.NotificationListResponse;
import com.realthon.etf.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /*
    GET "/notifications" : 알림 내역 조회
     */
    @GetMapping
    public NotificationListResponse getNotifications(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {

        return notificationService.getNotifications(userDetails.getUserId(), page, size);
    }

    /*
    POST "/notifications/{notificationId}/like" : 알림 내역 즐겨찾기 등록/취소
     */
    @PostMapping("/{notificationId}/like")
    public ResponseEntity<NotificationLikeResponse> notificationLike(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                     @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.notificationLike(notificationId, userDetails.getUserId()));
    }

    /*
    GET "/notifications/likes" : 즐겨찾기 리스트 조회
     */
    @GetMapping("/likes")
    public NotificationListResponse getLikes(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return notificationService.getLikedNotifications(userDetails.getUserId(), page, size);
    }

    /*
    GET "notifications/search" : 알림 내역 검색 기능
     */
    @GetMapping("/search")
    public NotificationListResponse search(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam String keyword,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return notificationService.search(userDetails.getUserId(), keyword, page, size);
    }
}
