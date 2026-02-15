package com.realthon.etf.email.service;

public interface EmailSender {

    void send(String to, String subject, String content);
}