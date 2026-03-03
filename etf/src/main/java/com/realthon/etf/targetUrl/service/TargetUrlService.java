package com.realthon.etf.targetUrl.service;

import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.recommendation.domain.Recommendation;
import com.realthon.etf.recommendation.repository.RecommendationRepository;
import com.realthon.etf.recommendation.service.RecommendationUpsertService;
import com.realthon.etf.recommendation.util.TitleExtractor;
import com.realthon.etf.targetUrl.domain.TargetUrl;
import com.realthon.etf.targetUrl.dto.request.TargetUrlRequest;
import com.realthon.etf.targetUrl.dto.response.TargetUrlListResponse;
import com.realthon.etf.targetUrl.dto.response.TargetUrlResponse;
import com.realthon.etf.targetUrl.repository.TargetUrlRepository;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TargetUrlService {

    private final TargetUrlRepository targetUrlRepository;
    private final UserRepository userRepository;
    private final RecommendationUpsertService recommendationUpsertService;

    /*
    target-url 등록
     */
    public TargetUrlResponse create(String loginId, TargetUrlRequest request) {
        User user = getUserByLoginId(loginId);
        String url = normalize(request.getTargetUrl());

        if (targetUrlRepository.existsByUser_UserIdAndTargetUrl(user.getUserId(), url)) {
            throw new CustomException(ExceptionCode.TARGET_URL_DUPLICATED);
        }

        TargetUrl saved = targetUrlRepository.save(
                TargetUrl.builder()
                        .user(user)
                        .targetUrl(url)
                        .build()
        );

        recommendationUpsertService.insertIfNotExists(url); // recommedation에 추가

        return TargetUrlResponse.from(saved);
    }

    /*
    target-url 수정
     */
    public TargetUrlResponse update(String loginId, Long targetUrlId, TargetUrlRequest request) {
        User user = getUserByLoginId(loginId);

        TargetUrl targetUrl = targetUrlRepository
                .findByTargetUrlIdAndUser_UserId(targetUrlId, user.getUserId())
                .orElseThrow(() -> new CustomException(ExceptionCode.TARGET_URL_NOT_FOUND));

        String newUrl = normalize(request.getTargetUrl());

        if (!targetUrl.getTargetUrl().equals(newUrl)
                && targetUrlRepository.existsByUser_UserIdAndTargetUrl(user.getUserId(), newUrl)) {
            throw new CustomException(ExceptionCode.TARGET_URL_DUPLICATED);
        }

        boolean changed = !targetUrl.getTargetUrl().equals(newUrl);
        targetUrl.updateUrl(newUrl);

        if (changed) {
            recommendationUpsertService.insertIfNotExists(newUrl); // recommedation에 추가
        }

        return TargetUrlResponse.from(targetUrl);
    }


    /*
    target-url 삭제
     */
    public void delete(String loginId, Long targetUrlId) {
        User user = getUserByLoginId(loginId);

        TargetUrl targetUrl = targetUrlRepository
                .findByTargetUrlIdAndUser_UserId(targetUrlId, user.getUserId())
                .orElseThrow(() -> new CustomException(ExceptionCode.TARGET_URL_NOT_FOUND));

        targetUrlRepository.delete(targetUrl);
    }

    /*
    target-url 리스트 조회
     */
    @Transactional(readOnly = true)
    public TargetUrlListResponse list(String loginId) {
        User user = getUserByLoginId(loginId);

        List<TargetUrlResponse> items = targetUrlRepository
                .findAllByUser_UserIdOrderByTargetUrlIdDesc(user.getUserId())
                .stream()
                .map(TargetUrlResponse::from)
                .toList();

        long totalCount = targetUrlRepository.countByUser_UserId(user.getUserId());

        return TargetUrlListResponse.of(totalCount, items);
    }

    // 공통 사용자 조회 로직
    private User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    // URL 정규화
    private String normalize(String url) {
        if (url == null) return null;
        String trimmed = url.trim();
        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
