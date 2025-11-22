# 2025 RE:ALThon 연합 해커톤 - 5팀 ETF backend
## 알려주잡(Job)
**알려주잡(Job)**은 사용자의 **이력서 한 번 등록**으로, 나에게 필요한 공지·채용·대외활동 정보를 **매일 자동으로 보내주는 AI 개인화 알림 서비스**입니다.  
이력서를 AI가 2단계로 분석해 강점·약점을 추출하고, 이를 바탕으로 학교·전공 학과 페이지, 채용 공고, 공모전 사이트 등 다양한 웹사이트를 크롤링하여 **약점을 보완할 수 있는 맞춤형 정보만 선별**합니다.  
추출된 정보는 OpenAI 기반 필터링으로 핵심만 정제된 뒤, 사용자의 대학·전공·관심사와 매칭되어 **카카오톡 알림**으로 전달되며, 대학생뿐 아니라 **취준생 및 일반 사용자까지 확장 가능한 서비스**를 지향합니다.

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
