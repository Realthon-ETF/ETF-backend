package com.realthon.etf.notification.service;

import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.notification.domain.Notification;
import com.realthon.etf.notification.dto.response.NotificationLikeResponse;
import com.realthon.etf.notification.dto.response.NotificationListResponse;
import com.realthon.etf.notification.dto.response.NotificationResponse;
import com.realthon.etf.notification.repository.NotificationRepository;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    /*
    알림 내역 조회
     */
    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Notification> result = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        List<NotificationResponse> items = result.getContent().stream()
                .map(NotificationResponse::from)
                .toList();

        return NotificationListResponse.of(result.getTotalElements(), items);
    }

    /*
    알림 즐겨찾기 등록/취소
     */
    @Transactional
    public NotificationLikeResponse notificationLike(Long notificationId, Long userId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOTIFICATION_NOT_FOUND));
        if (!notification.getUser().getUserId().equals(userId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN);
        }

        boolean isLiked;

        if (notification.isLiked()) {
            notification.unlike();
            isLiked = false;
        } else {
            notification.like();
            isLiked = true;
        }

        return NotificationLikeResponse.builder()
                .notificationId(notification.getNotificationId())
                .isLiked(isLiked)
                .build();
    }

    /*
    알림 즐겨찾기 리스트 조회
     */
    @Transactional(readOnly = true)
    public NotificationListResponse getLikedNotifications(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> result = notificationRepository.findLikedByUser(userId, pageable);

        List<NotificationResponse> items = result.getContent().stream()
                .map(NotificationResponse::from)
                .toList();

        return NotificationListResponse.of(result.getTotalElements(), items);
    }

    /*
    알림 내역 검색 기능
     */
    @Transactional(readOnly = true)
    public NotificationListResponse search(Long userId, String keyword, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> result = notificationRepository.searchByUser(userId, keyword, pageable);

        List<NotificationResponse> items = result.getContent().stream()
                .map(NotificationResponse::from)
                .toList();

        return NotificationListResponse.of(result.getTotalElements(), items);
    }

}
