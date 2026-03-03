package com.realthon.etf.targetUrl.repository;

import com.realthon.etf.targetUrl.domain.TargetUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TargetUrlRepository extends JpaRepository<TargetUrl, Long> {

    boolean existsByUser_UserIdAndTargetUrl(Long userId, String targetUrl);
    Optional<TargetUrl> findByTargetUrlIdAndUser_UserId(Long targetUrlId, Long userId);
    List<TargetUrl> findAllByUser_UserIdOrderByTargetUrlIdDesc(Long userId);
    long countByUser_UserId(Long userId);

    @Query("SELECT t.targetUrl FROM TargetUrl t WHERE t.user.userId = :userId")
    List<String> findUrlsByUserId(@Param("userId") Long userId);

    @Query("select count(t) > 0 from TargetUrl t where t.user.userId = :userId and t.targetUrl = :url")
    boolean existsByUserIdAndUrl(@Param("userId") Long userId, @Param("url") String url);

}
