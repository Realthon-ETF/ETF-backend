package com.realthon.etf.user.dto.response;

import com.realthon.etf.user.domain.UserResumeSummary;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResumeSummaryResponse {

    private Long userId;
    private String summary;

    public static UserResumeSummaryResponse from(UserResumeSummary entity) {
        return UserResumeSummaryResponse.builder()
                .userId(entity.getUser().getUserId())
                .summary(entity.getSummary())
                .build();
    }
}
