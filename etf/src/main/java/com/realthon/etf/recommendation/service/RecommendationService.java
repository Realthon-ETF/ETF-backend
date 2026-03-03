package com.realthon.etf.recommendation.service;

import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.recommendation.domain.Recommendation;
import com.realthon.etf.recommendation.repository.RecommendationRepository;
import com.realthon.etf.targetUrl.repository.TargetUrlRepository;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final TargetUrlRepository targetUrlRepository;
    private final UserRepository userRepository;

    // 추천 url 조회
    public List<Recommendation> getRandom5ForUser(Long userId) {
        getUserById(userId);

        List<String> excludedUrls = targetUrlRepository.findUrlsByUserId(userId);

        if (excludedUrls == null || excludedUrls.isEmpty()) {
            return recommendationRepository.findRandom5();
        }
        return recommendationRepository.findRandom5ExcludeUrls(excludedUrls);
    }

    // 공통 사용자 조회 로직
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }
}