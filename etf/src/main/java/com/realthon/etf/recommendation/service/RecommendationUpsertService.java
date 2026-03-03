package com.realthon.etf.recommendation.service;

import com.realthon.etf.recommendation.domain.Recommendation;
import com.realthon.etf.recommendation.repository.RecommendationRepository;
import com.realthon.etf.recommendation.util.TitleExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecommendationUpsertService {

    private final RecommendationRepository recommendationRepository;
    private final TitleExtractor titleExtractor;

    @Transactional
    public void insertIfNotExists(String url) {
        if (recommendationRepository.existsByUrl(url)) return;

        try {
            recommendationRepository.save(
                    Recommendation.builder()
                            .title(titleExtractor.extractTitle(url))
                            .url(url)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
        }
    }

    private String cut(String s, int max) {
        if (s == null) return null;
        s = s.trim();
        return s.length() <= max ? s : s.substring(0, max);
    }
}