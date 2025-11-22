package com.realthon.etf.notification.repository;

import com.realthon.etf.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 최신순 정렬
    List<Notification> findAllByOrderByCreatedAtDesc();
}
