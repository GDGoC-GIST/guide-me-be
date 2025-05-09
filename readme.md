# 📅 Guide-me: AI based Semester Scheduler

사용자의 시간표를 바탕으로 다음 학기 시간표를 추천해주는 시스템입니다.

---

## 📦 프로젝트 구조

### 1. `auth-service`

- 사용자의 인증 및 인가를 담당합니다.
- JWT 기반 인증 시스템을 사용하며, Firebase를 통해 인증 정보를 검증합니다.
- 주요 기능:
  - 회원가입 및 로그인
  - 토큰 발급 및 갱신
  - 사용자 권한 처리

### 2. `user-service`

- 사용자의 시간표 및 추천 정보를 관리합니다.
- 데이터베이스에 저장된 시간표를 바탕으로 다음 학기를 추천하는 알고리즘을 제공합니다.
- 주요 기능:
  - 시간표 CRUD
  - 추천 시간표 조회
  - 사용자 정보 조회 및 수정

---

## ⚙️ 기술 스택

- Java 17
- Spring Boot 3.4.4
- Spring Security
- MySQL
- Firebase

---

## 🌿 Git 전략

- `master`: 실제 배포 가능한 안정 버전
- `develop`: 통합 개발 브랜치
- `feature/[task이름]`: 개별 작업 브랜치

### ✔️ Pull Request 절차

1. 코드 푸시 → Pull Request 오픈
2. CI 파이프라인에서 자동 빌드 및 테스트
3. Test Coverage 70% 이상 필요
4. 팀원 최소 1명 이상의 Approve
5. Merge는 develop 브랜치로

---

## 🧑‍💻 협업 규칙

- 모든 작업은 이슈(Task)로 관리하며, feature 브랜치 명에 Task 이름 포함
- PR 전에 로컬 테스트 필수
- 리뷰어가 코드 스멜, 로직 오류, 테스트 누락 등을 확인
- 필요 시 리팩토링 요청 및 재리뷰
- Merge 조건:
  - 테스트 커버리지 70% 이상
  - 1명 이상의 Approve

---

## 📝 문서화

- 변경 사항 및 개발 절차에 대한 문서 지속 업데이트
