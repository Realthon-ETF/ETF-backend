package com.realthon.etf.user.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterestField {

    // 영업/고객상담
    GENERAL_SALES("영업/고객상담 - 일반영업 - 일반영업"),
    B2B_SALES("영업/고객상담 - B2B영업/기술영업 - B2B영업"),
    TECH_SALES("영업/고객상담 - B2B영업/기술영업 - 기술영업"),
    SALES_MANAGEMENT("영업/고객상담 - 영업관리/지원/영업기획 - 영업관리"),
    SALES_SUPPORT("영업/고객상담 - 영업관리/지원/영업기획 - 지원"),
    SALES_PLANNING("영업/고객상담 - 영업관리/지원/영업기획 - 영업기획"),
    STORE_SALES("영업/고객상담 - 판매/캐셔/매장관리 - 판매"),
    CASHIER("영업/고객상담 - 판매/캐셔/매장관리 - 캐셔"),
    STORE_MANAGEMENT("영업/고객상담 - 판매/캐셔/매장관리 - 매장관리"),
    OUTBOUND_TM("영업/고객상담 - 아웃바운드 TM - 아웃바운드 TM"),
    CUSTOMER_MANAGEMENT("영업/고객상담 - 고객관리/인바운드 - 고객관리"),
    INBOUND("영업/고객상담 - 고객관리/인바운드 - 인바운드"),

    GENERAL_SALES_ETC("영업/고객상담 - 일반영업 - 기타"),
    B2B_SALES_ETC("영업/고객상담 - B2B영업/기술영업 - 기타"),
    SALES_OPERATION_ETC("영업/고객상담 - 영업관리/지원/영업기획 - 기타"),
    STORE_OPERATION_ETC("영업/고객상담 - 판매/캐셔/매장관리 - 기타"),
    OUTBOUND_TM_ETC("영업/고객상담 - 아웃바운드 TM - 기타"),
    CUSTOMER_SUPPORT_ETC("영업/고객상담 - 고객관리/인바운드 - 기타"),

    // 경영/사무
    BUSINESS_PLANNING("경영/사무 - 기획/전략/경영 - 사업기획"),
    SERVICE_PLANNING("경영/사무 - 기획/전략/경영 - 서비스기획/운영"),
    NEW_BUSINESS("경영/사무 - 기획/전략/경영 - 신규사업/BD"),
    STRATEGY_PLANNING("경영/사무 - 기획/전략/경영 - 전략기획"),
    CONTENT_PLANNING("경영/사무 - 기획/전략/경영 - 콘텐츠기획/운영"),
    MNA("경영/사무 - 기획/전략/경영 - M&A/타당성분석/사업제휴"),

    HR("경영/사무 - 인사/노무/교육 - 인사"),
    LABOR("경영/사무 - 인사/노무/교육 - 노무"),
    RECRUITMENT("경영/사무 - 인사/노무/교육 - 채용/면접"),
    EDUCATION_PLANNING("경영/사무 - 인사/노무/교육 - 교육기획"),

    ACCOUNTING("경영/사무 - 재무/세무/IR - 회계"),
    FINANCE("경영/사무 - 재무/세무/IR - 재무"),
    TAX("경영/사무 - 재무/세무/IR - 세무"),
    IR("경영/사무 - 재무/세무/IR - IR"),

    OFFICE_ASSISTANT("경영/사무 - 사무보조/문서작성 - 사무보조"),
    DOCUMENT_WRITING("경영/사무 - 사무보조/문서작성 - OA/문서작성"),
    WEBSITE_MANAGEMENT("경영/사무 - 사무보조/문서작성 - 홈페이지관리"),

    BUSINESS_STRATEGY_ETC("경영/사무 - 기획/전략/경영 - 기타"),
    OFFICE_ADMIN_ETC("경영/사무 - 사무/총무/법무 - 기타"),
    HR_ETC("경영/사무 - 인사/노무/교육 - 기타"),
    ACCOUNTING_ETC("경영/사무 - 경리/회계/결산 - 기타"),
    FINANCE_ETC("경영/사무 - 재무/세무/IR - 기타"),
    OFFICE_SUPPORT_ETC("경영/사무 - 사무보조/문서작성 - 기타"),

    // 마케팅
    MARKETING_STRATEGY("마케팅/광고/홍보 - 마케팅/PR/분석 - 마케팅전략/기획"),
    AD_PLANNING("마케팅/광고/홍보 - 마케팅/PR/분석 - 광고기획"),
    ONLINE_MARKETING("마케팅/광고/홍보 - 마케팅/PR/분석 - 온라인마케팅"),
    MARKET_RESEARCH("마케팅/광고/홍보 - 마케팅/PR/분석 - 시장조사/분석"),
    BRAND_MANAGER("마케팅/광고/홍보 - 마케팅/PR/분석 - 브랜드매니저"),
    GLOBAL_MARKETING("마케팅/광고/홍보 - 마케팅/PR/분석 - 해외마케팅"),
    PR("마케팅/광고/홍보 - 마케팅/PR/분석 - 언론홍보/PR"),
    SURVEY_RESEARCH("마케팅/광고/홍보 - 마케팅/PR/분석 - 설문/리서치"),

    MARKETING_ETC("마케팅/광고/홍보 - 마케팅/PR/분석 - 기타"),
    EXHIBITION_ETC("마케팅/광고/홍보 - 전시/컨벤션 - 기타"),

    // IT
    DOTNET_DEV("IT/인터넷 - 웹개발 - .NET 개발자"),
    NODE_DEV("IT/인터넷 - 웹개발 - Node.js 개발자"),
    PHP_DEV("IT/인터넷 - 웹개발 - PHP 개발자"),
    RUBY_DEV("IT/인터넷 - 웹개발 - 루비온레일즈 개발자"),
    WEB_DEV("IT/인터넷 - 웹개발 - 웹 개발자"),
    WEB_PROGRAMMER("IT/인터넷 - 웹개발 - 웹프로그래머"),
    JAVA_DEV("IT/인터넷 - 웹개발 - 자바 개발자"),
    FRONTEND_DEV("IT/인터넷 - 웹개발 - 프론트엔드 개발자"),

    ANDROID_DEV("IT/인터넷 - 응용프로그램개발 - Android앱개발"),
    IOS_DEV("IT/인터넷 - 응용프로그램개발 - iOS 개발자"),
    SOFTWARE_ENGINEER("IT/인터넷 - 응용프로그램개발 - 소프트웨어 엔지니어"),

    DEVOPS_ENGINEER("IT/인터넷 - ERP/시스템개발/설계 - DevOps/시스템 관리자"),
    SYSTEM_ENGINEER("IT/인터넷 - ERP/시스템개발/설계 - 시스템엔지니어(SE)"),
    EMBEDDED_DEV("IT/인터넷 - ERP/시스템개발/설계 - 임베디드 개발자"),

    NETWORK_ENGINEER("IT/인터넷 - 네트워크/서버/보안 - 네트워크엔지니어"),
    SECURITY_ENGINEER("IT/인터넷 - 네트워크/서버/보안 - 보안 엔지니어"),
    SERVER_DEV("IT/인터넷 - 네트워크/서버/보안 - 서버 개발자"),

    DATA_ENGINEER("IT/인터넷 - DBA/데이터베이스 - 데이터 엔지니어"),
    PYTHON_DEV("IT/인터넷 - DBA/데이터베이스 - 파이썬 개발자"),
    BI_ENGINEER("IT/인터넷 - DBA/데이터베이스 - BI 엔지니어"),

    WEB_DEV_ETC("IT/인터넷 - 웹개발 - 기타"),
    APP_DEV_ETC("IT/인터넷 - 응용프로그램개발 - 기타"),
    SYSTEM_DEV_ETC("IT/인터넷 - ERP/시스템개발/설계 - 기타"),
    NETWORK_SECURITY_ETC("IT/인터넷 - 네트워크/서버/보안 - 기타"),
    DB_ETC("IT/인터넷 - DBA/데이터베이스 - 기타"),

    ETC("기타 - 기타 - 기타");

    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static InterestField fromJson(String value) {
        if (value == null) return null;

        for (InterestField f : values()) {
            if (f.name().equalsIgnoreCase(value)) {
                return f;
            }
        }

        for (InterestField f : values()) {
            if (f.label.equals(value)) {
                return f;
            }
        }

        throw new IllegalArgumentException("Invalid InterestField: " + value);
    }
}