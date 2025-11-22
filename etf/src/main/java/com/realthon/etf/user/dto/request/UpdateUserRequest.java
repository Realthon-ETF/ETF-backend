package com.realthon.etf.user.dto.request;

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

    @Size(max = 100)
    private String interestField;

    @Min(1)
    private Long intervalDays;

    private LocalTime alarmTime;    // "09:00" 같은 형태로 들어온다고 가정
}
