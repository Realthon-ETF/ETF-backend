package com.realthon.etf.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 전체
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ClientExceptionCode.INTERNAL_SERVER_ERROR, "예상치 못한 서버에러가 발생했습니다."),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, ClientExceptionCode.ILLEGAL_ARGUMENT, "필수 파라미터 누락"),

    // 인증
    LOGIN_ERROR(HttpStatus.UNAUTHORIZED, ClientExceptionCode.LOGIN_ERROR, "로그인 인증 실패했습니다."),
    AUTH_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ClientExceptionCode.AUTH_SERVER_ERROR, "인증 서버와 통신 중 오류가 발생했습니다."),
    AUTH_TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, ClientExceptionCode.AUTH_TOKEN_EMPTY, "인증 토큰이 존재하지 않습니다. 다시 로그인해주세요."),
    ACCESS_TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, ClientExceptionCode.ACCESS_TOKEN_EMPTY, "엑세스 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, ClientExceptionCode.REFRESH_TOKEN_EMPTY, "리프레시 토큰이 존재하지 않습니다."),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ClientExceptionCode.AUTH_TOKEN_EXPIRED, "만료된 토큰입니다."),
    AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, ClientExceptionCode.AUTH_TOKEN_INVALID, "올바르지 않은 토큰입니다."),
    AUTH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, ClientExceptionCode.AUTH_TOKEN_MISMATCH, "토큰 소유자가 일치하지 않습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, ClientExceptionCode.FORBIDDEN, "해당 리소스에 대한 권한이 없습니다."),


    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, ClientExceptionCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,ClientExceptionCode.INVALID_PASSWORD, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, ClientExceptionCode.DUPLICATE_LOGIN_ID, "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, ClientExceptionCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, ClientExceptionCode.DUPLICATE_PHONE_NUMBER, "이미 사용 중인 전화번호입니다."),

    // 이력서
    RESUME_FILE_REQUIRED(HttpStatus.BAD_REQUEST, ClientExceptionCode.RESUME_FILE_REQUIRED, "이력서 PDF 파일을 업로드해 주세요."),
    RESUME_SUMMARY_NOT_FOUND(HttpStatus.NOT_FOUND, ClientExceptionCode.RESUME_SUMMARY_NOT_FOUND, "이력서 요약본이 없습니다. 먼저 이력서 PDF 업로드를 해주세요."),

    // 내 웹사이트
    TARGET_URL_NOT_FOUND(HttpStatus.NOT_FOUND, ClientExceptionCode.TARGET_URL_NOT_FOUND, "존재하지 않는 내 웹 사이트입니다."),
    TARGET_URL_DUPLICATED(HttpStatus.CONFLICT, ClientExceptionCode.TARGET_URL_DUPLICATED, "이미 등록한 웹 사이트입니다."),

    // 알림
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, ClientExceptionCode.NOTIFICATION_NOT_FOUND, "존재하지 않는 알림입니다."),

    // AI
    AI_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, ClientExceptionCode.AI_REQUEST_NOT_FOUND, "AI 요청이 존재하지 않습니다."),
    AI_CALLBACK_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ClientExceptionCode.AI_CALLBACK_UNAUTHORIZED, "AI 콜백 인증 실패함"),
    AI_ALREADY_COMPLETED(HttpStatus.CONFLICT, ClientExceptionCode.AI_ALREADY_COMPLETED, "이미 완료된 AI 요청임"),
    AI_CRAWLER_CALL_FAILED(HttpStatus.BAD_GATEWAY, ClientExceptionCode.AI_CRAWLER_CALL_FAILED, "AI 크롤러 호출 실패함"),

    // 이메일 인증
    EMAIL_VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, ClientExceptionCode.EMAIL_VERIFICATION_CODE_INVALID, "이메일 인증번호가 올바르지 않습니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, ClientExceptionCode.EMAIL_VERIFICATION_CODE_EXPIRED, "이메일 인증번호가 만료되었습니다."),
    EMAIL_VERIFICATION_REQUIRED(HttpStatus.BAD_REQUEST, ClientExceptionCode.EMAIL_VERIFICATION_REQUIRED,  "이메일 인증이 필요합니다."),

    // 비밀번호 재설정
    PASSWORD_RESET_TOKEN_INVALID(HttpStatus.BAD_REQUEST, ClientExceptionCode.PASSWORD_RESET_TOKEN_INVALID,"비밀번호 재설정 토큰이 올바르지 않습니다."),
    PASSWORD_RESET_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, ClientExceptionCode.PASSWORD_RESET_TOKEN_EXPIRED,"비밀번호 재설정 토큰이 만료되었습니다."),

    // 추천
    RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, ClientExceptionCode.RECOMMENDATION_NOT_FOUND, "추천 정보를 찾을 수 없습니다."),
    TARGET_URL_ALREADY_EXISTS(HttpStatus.CONFLICT, ClientExceptionCode.TARGET_URL_ALREADY_EXISTS, "이미 등록된 URL입니다.");

    private final HttpStatus httpStatus;
    private final ClientExceptionCode clientExceptionCode;
    private final String message;

    ExceptionCode(HttpStatus httpStatus, ClientExceptionCode clientExceptionCode, String message) {
        this.httpStatus = httpStatus;
        this.clientExceptionCode = clientExceptionCode;
        this.message = message;
    }
}
