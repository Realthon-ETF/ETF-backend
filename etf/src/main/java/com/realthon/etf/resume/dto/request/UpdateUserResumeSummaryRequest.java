package com.realthon.etf.resume.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserResumeSummaryRequest {

    @Size(max = 4000)
    private String summary;
}
