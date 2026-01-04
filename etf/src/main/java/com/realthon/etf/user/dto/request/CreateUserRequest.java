package com.realthon.etf.user.dto.request;

import com.realthon.etf.user.domain.InterestField;
import com.realthon.etf.user.domain.User;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    @Pattern(regexp = "^[0-9\\-]{9,15}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;

    @Size(max = 50)
    private String school;

    @Size(max = 50)
    private String major;

    @NotNull
    private InterestField interestField;

    @Min(1)
    private Long intervalDays;

    private LocalTime alarmTime;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .loginId(getLoginId())
                .password(encodedPassword)
                .username(getUsername())
                .phoneNumber(getPhoneNumber())
                .email(getEmail())
                .school(getSchool())
                .major(getMajor())
                .interestField(getInterestField())
                .intervalDays(getIntervalDays())
                .alarmTime(getAlarmTime())
                .build();
    }
}
