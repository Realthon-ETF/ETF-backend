package com.realthon.etf.notification.repository;

import com.realthon.etf.notification.domain.Notification;
import com.realthon.etf.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("""
        SELECT n
        FROM Notification n
        WHERE n.user.userId = :userId
          AND n.isLiked = true
        ORDER BY n.createdAt DESC
    """)
    Page<Notification> findLikedByUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
        SELECT n
        FROM Notification n
        WHERE n.user.userId = :userId
          AND (
               LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(n.sourceName) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY n.createdAt DESC
    """)
    Page<Notification> searchByUser(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
