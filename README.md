# 2025 RE:ALThon 연합 해커톤 - 5팀 ETF backend

### 백엔드 주요 기능 정리
- JWT 기반 회원가입 / 로그인
- 학교·전공·관심분야 등록
- OPENAI API로 이력서 pdf 요약
- 요약된 이력서 관리
- 발송된 알림 목록 조회

### 기술 스택
- Backend: Spring Boot, Java 17, JPA
- DB: PostgreSQL 
- Infra: AWS EC2, RDS, CODEDEPLOY, github actions
- 기타 : OpenAI API

### 프로젝트 구조

```text
src
└─ main
   └─ java
      └─ com
         └─ realthon
            └─ etf
               ├─ ai
               ├─ auth
               │  ├─ controller
               │  ├─ dto
               │  ├─ jwt
               │  └─ service
               ├─ global
               │  └─ config
               ├─ notification
               │  ├─ controller
               │  ├─ domain
               │  ├─ dto
               │  ├─ repository
               │  └─ service
               ├─ resume
               │  ├─ controller
               │  ├─ service
               │  └─ PdfTextExtractor.java
               ├─ user
               │  ├─ controller
               │  ├─ domain
               │  ├─ dto
               │  ├─ repository
               │  └─ service
               └─ EtfApplication.java
