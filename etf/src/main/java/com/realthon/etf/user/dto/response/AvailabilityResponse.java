package com.realthon.etf.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvailabilityResponse {

    private boolean available;

    public static AvailabilityResponse from(boolean available) {
        return AvailabilityResponse.builder()
                .available(available)
                .build();
    }
}
