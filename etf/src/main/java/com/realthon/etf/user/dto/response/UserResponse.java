package com.realthon.etf.user.dto.response;

import com.realthon.etf.user.domain.InterestField;
import com.realthon.etf.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Set;

@Getter
@Builder
public class UserResponse {

    private Long userId;
    private String loginId;
    private String username;
    private String phoneNumber;
    private String email;
    private String school;
    private String major;
    private Set<InterestField> interestFields;
    private Long intervalDays;
    private LocalTime alarmTime;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .school(user.getSchool())
                .major(user.getMajor())
                .interestFields(user.getInterestFields())
                .intervalDays(user.getIntervalDays())
                .alarmTime(user.getAlarmTime())
                .build();
    }
}
