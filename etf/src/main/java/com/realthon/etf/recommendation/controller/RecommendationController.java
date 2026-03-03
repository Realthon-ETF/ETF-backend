package com.realthon.etf.recommendation.controller;

import com.realthon.etf.auth.dto.CustomUserDetails;
import com.realthon.etf.recommendation.domain.Recommendation;
import com.realthon.etf.recommendation.dto.response.RecommendationListResponse;
import com.realthon.etf.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationQueryService;

    /*
    랜덤 추천 5개 조회
     */
    @GetMapping
    public List<RecommendationListResponse> getRandom5(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Recommendation> list = recommendationQueryService.getRandom5ForUser(userDetails.getUserId());
        return list.stream().map(RecommendationListResponse::from).toList();
    }
}