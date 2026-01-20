package com.realthon.etf.global.util;

import java.util.UUID;

public class RequestIdGenerator {
    public static String newRequestId() {
        return UUID.randomUUID().toString();
    }
}