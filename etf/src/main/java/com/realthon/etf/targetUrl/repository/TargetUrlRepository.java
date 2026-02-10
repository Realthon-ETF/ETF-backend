package com.realthon.etf.targetUrl.repository;

import com.realthon.etf.targetUrl.domain.TargetUrl;
import com.realthon.etf.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TargetUrlRepository extends JpaRepository<TargetUrl, Long> {

    boolean existsByUser_UserIdAndTargetUrl(Long userId, String targetUrl);
    Optional<TargetUrl> findByTargetUrlIdAndUser_UserId(Long targetUrlId, Long userId);
    List<TargetUrl> findAllByUser_UserIdOrderByTargetUrlIdDesc(Long userId);
    long countByUser_UserId(Long userId);
    List<TargetUrl> findAllByUser_UserId(Long userId);
}
