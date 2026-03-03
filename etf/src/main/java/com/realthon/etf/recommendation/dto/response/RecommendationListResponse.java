package com.realthon.etf.recommendation.dto.response;

import com.realthon.etf.recommendation.domain.Recommendation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationListResponse {

    private Long recommendationId;
    private String title;
    private String url;

    public static RecommendationListResponse from(Recommendation r) {
        return RecommendationListResponse.builder()
                .recommendationId(r.getRecommendationId())
                .title(r.getTitle())
                .url(r.getUrl())
                .build();
    }
}
