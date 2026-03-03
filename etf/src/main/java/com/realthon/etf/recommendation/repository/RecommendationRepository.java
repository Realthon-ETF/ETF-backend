package com.realthon.etf.recommendation.repository;

import com.realthon.etf.recommendation.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    // targetUrl이 비어있지 않은 경우-제외하고 랜덤 5개
    @Query(value = """
        SELECT *
        FROM recommendation r
        WHERE r.url NOT IN (:excludedUrls)
        ORDER BY RANDOM()
        LIMIT 5
        """, nativeQuery = true)
    List<Recommendation> findRandom5ExcludeUrls(@Param("excludedUrls") List<String> excludedUrls);

    // targetUrl이 비어있는 경우-그냥 랜덤 5개
    @Query(value = """
        SELECT *
        FROM recommendation r
        ORDER BY RANDOM()
        LIMIT 5
        """, nativeQuery = true)
    List<Recommendation> findRandom5();

    Optional<Recommendation> findByUrl(String url);
    boolean existsByUrl(String url);
}