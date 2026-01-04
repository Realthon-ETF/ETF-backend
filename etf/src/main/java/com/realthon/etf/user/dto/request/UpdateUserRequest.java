package com.realthon.etf.user.dto.request;

import com.realthon.etf.user.domain.InterestField;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(max = 30)
    private String username;

    @Pattern(regexp = "^[0-9\\-]{9,15}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @Email
    private String email;

    @Size(max = 50)
    private String school;

    @Size(max = 50)
    private String major;

    private InterestField interestField;

    @Min(1)
    private Long intervalDays;

    private LocalTime alarmTime;

    @Size(max = 4000)
    private String resumeSummary;
}
