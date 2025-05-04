# couponmoa (기존 모놀리식 API 서버)

## 📌 프로젝트 개요

이 레포지토리는 **MSA 구조로 분리되기 이전**, 알림과 스케줄링 이외의 모든 API기능이 하나의 서버에서 처리되던 **초기 Monolithic 구조의 백엔드 서버**입니다.
해당 레포지토리는 **기록 및 이관을 위해 보존 중이며**, 더 이상 유지보수나 신규 기능 개발은 이루어지지 않습니다.  

현재는 기능별로 다음과 같이 **마이크로서비스화** 되어 분리되었습니다:

- `couponmoa-user`: 사용자 관련 기능
- `couponmoa-coupon`: 쿠폰 발행/조회/사용 등
- `couponmoa-store`: 매장 관련 기능
- `couponmoa-notification`: 알림 처리 (SSE, Email 등)
- `couponmoa-scheduler`: 예약 작업 및 쿠폰 만료 처리
- `couponmoa-ai`: AI 추천 로직
- `couponmoa-gateway`: 인증 및 API Gateway 역할

---

## 🏗️ 초기 구조

해당 서버는 다음과 같은 기능을 모두 포함하고 있습니다:

- 사용자 등록 및 인증
- 매장 생성/조회/수정/삭제
- 쿠폰 생성/조회/수정/삭제/검색
- 쿠폰 발급/조회/사용처리
- 매장, 쿠폰 구독 및 구독 알림

---

## 📦 기술 스택 (Monolithic 기준)

- Java 17
- Spring Boot 3.x
- JPA, MySQL
- JWT, Spring Security
- Redis, Elasticsearch
- Gradle
- SMTP
- QueryDSL
- Swagger
- S3
- Cloudfront
- SQS(알림 서버와 통신)

---

  
## 쿠폰 수량 개념 정리 
- totalQuantity : 생생된 쿠폰의 총 개수. (create,update시 변경가능)
- availableQuantity : 현재 발급 가능한 쿠폰의 잔여 개수.
- issuedCouponQuantity : totalQuantity-availableQuantity, 즉 사용자에게 발급된 쿠폰의 총 개수.

- 쿠폰 생성 시 : totalQuantity의 경우 요청 값으로 생성, availableQuantity는 tQ의 값으로 생성(초기 생성시 총 개수만큼 발급이 가능)
- 쿠폰 사용 시 : 사용자가 쿠폰을 발급받으면 useCoupon() 메서드를 통해 availableQuantity 감소 (동시성 제어 예정)
- 쿠폰 수정 시(쿠폰 수량 수정) : 요청 값으로 새로운 총 쿠폰 개수(newTotalQuantity)를 받음, ,
  availableQuantity는 new TotalQuantity - issuedQuantity로 자동 설정됨.
  newTotalQuantity는 이미 발급된 쿠폰 개수(issuedCouponQuantity) 보다 작을 수 없음.
  단, availableQuantity가 아직 남아있을때, 이전의 totalQuantity보다 작은 값을 넣어, availableQuantity를 줄이는 방식으로는 동작가능.

  ### 쿠폰 Status 변경되는 시점 정리
  - 1. 생성될 시, 날짜 검증(제약) 조건으로 UPCOMING으로만 생성됨
    2. update 시, date 값들의 변경에따라 Status값이 변경될 수 있음.
    3. 조회한 값을 반환할 때, dto 단에서 Status값이 변경되어 반환 됨
    4. availableQuantity가 0이 되었을때 ( 모두 발급되어, 소진되었을 떄) ENDED로 변경됨.
  
