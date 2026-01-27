package com.realthon.etf.ai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AiUserProfile {

    private String username;
    private String phoneNumber;
    private String school;
    private String major;
    private List<String> interestFields;
    private Integer intervalDays;
    private LocalTime alarmTime;
}
