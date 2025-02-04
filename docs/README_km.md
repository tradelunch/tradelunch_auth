# 카카오 모빌리티 과제

---

## 개발환경

---
SpringBoot 프로젝트는 springboot initializer를 활용해 생성했습니다. 개발환경은 아래와 같습니다.
```bash
java adopt-openjdk-15

# 프로젝트 및 빌드 환경 설정
project 설정 java 15
gradle 설정 build java 15

spring 5.3.5
springboot 2.4.4

kotlin version 1.4.31

# 코틀린 앱이 동작환경 설정 (build.gradle.kts)
java.sourceCompatibility = JavaVersion.VERSION_15
jvmTarget = "15"

mysql 8.0.23
gradle 6.8.3
```

#### 인텔리J 사용 시 IDE 환경 설정 확인
```shell
# 프로젝트 환경 설정
cmd + ;(project structure) > project > project SDK java 버전 확인

# gradle 빌드 실행 설정
cmd + ,(preference > 
  build, Execution, Deployment > 
    Build Tools > 
      gradle: gradle JVM SDK java 버전 확인
```

#### Java 15
제가 사용한 java 15 설정 방법 입니다.
인텔리J project structure 내 project SDK에서 java를 직접 다운로드하여 사용했습니다.
![project SDK java 버전 다운로드](doc/projectSDK.png)

다운로드한 자바 SDK HomePath를 확인 후 jenv를 활용해 터미널에서 환경 설정 해주었습니다.
![java home path in intellij](doc/javaSDKHomePath.png)

```shell
brew install jenv
jenv add /Users/super/Library/Java/JavaVirtualMachines/adopt-openjdk-15.0.2/Contents/Home
jenv global 15
```

## Build 및 실행

---
### DB 설정
```shell
# DB 설정 macOS 기준 설치 및 설정에 대한 내용 입니다. 
brew doctor
brew update
brew search myql
brew install mysql
brew services start mysql

# 다른 OS 사용 시 mysql 버전 8 설치 후 아래 설정 하시면 됩니다
mysql -u root -p
create database mobility default character set utf8;
show databases;

create user 'smartcity'@'%' identified by 'wemovelife';
grant all privileges on gorgeous.* to 'smartcity'@'%' WITH GRANT OPTION;

select user, host, db from mysql.db;

# database mobility
# user smart
# passowrd wemovelife
```
- application.yml DB dataSource root 기본설정
    - 사용자 추가 시 smartcity 사용자 생성 후 application.yml 수정

### gradle 빌드 후 && 실행
```bash
# 프로젝트 압축 해제 후
cd $압축해제 폴더

# 빌드 환경에 맞는 profile을 선택 후 build
./gradlew -Pprofile=dev clean build

# 빌드 환경에 맞는 profile을 선택해 실행
## 기본 profiles.active: test 로 되어 있어 그냥 실행 시 더미 데이터 초기화
java -jar build/libs/biiling-0.0.1-SNAPSHOT.war

## 실행 시 DB dummy 초기호 되지 않는다.
java -Dspring.profiles.active=dev -jar build/libs/biiling-0.0.1-SNAPSHOT.war

```

#### 추가: Profile 별 프로젝트 실행
프로젝트 진행 시 아래와 같이 배포 환경별로 속성을 관리할 수 있습니다.
```bash
# 앱 실행 기본 주 설정 파일
main/
  resources/
    application.yml

java -jar build/libs/biiling-0.0.1-SNAPSHOT.war

# 앱 실행 시 spring.profiles.active: dev 시 아래 설정 사용 
# (sql 코멘트 및 statistics 비 활성화)
main/
  resources/
    dev/
      application-dev.yml

java -Dspring.profiles.active=dev -jar build/libs/biiling-0.0.1-SNAPSHOT.war

# 앱 test 시 설정은 아래 설정 사용
test/
  resources/
    application.yml
```

## 구조

---

### 클래스 다이어그램
![클래스 관계도](./doc/diagram.png)


### DB Schema && 구조
```sql
CREATE TABLE `coupon` (
  `coupon_id` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `discount_amount` int NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `usable_from` datetime(6) DEFAULT NULL,
  `usable_until` datetime(6) DEFAULT NULL,
  `use_min_amount` int NOT NULL,
  PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```
- 쿠폰 테이블 하나 사용 하였습니다.

### 모델 설계
Entity
- BaseTimeEntity: createAt, updatedAt 구현, updatedAt 자동 갱신
- Coupon:
    - BaseTimeEntity: createAt, updateAt 상속
    - Comparable: compareTo 구현 (usableUntil Asc, discountAmount Desc)
    - toString, hashcode, equal 구현 (kassava 라이브러리)
    - validate: 쿠폰 사용 가능 여부 validation

DTO
- UseResult: 상품 금액에 따른 쿠폰 사용 결과 저장
    - appliedDiscountAmount: 실제 할인 적용 금액
    - amounToPay: 실제 지불해야할 금액
    - prie: 상품 금액
    - coupon: 사용한 쿠폰


### API 구조

```shell
# 연결 확인
GET /ping HTTP/1.1
Host: localhost:3000

# 쿠폰 목록 불러오기
GET /api/coupon/list HTTP/1.1
Host: localhost:3000

# price를 기준으로 사용 가능한 쿠폰 목록 호출 @Query JPQL 이용한 호출
GET /api/coupon/list/price/query/:price HTTP/1.1
Host: localhost:3000

# 위와 동일한 기능 JPA method를 이용해 결과 호출
GET /api/coupon/list/price/jpa/:price HTTP/1.1
Host: localhost:3000

# 사용할 쿠폰 ID와 금액을 입력 받아 구폰 사용 결과 반환
GET /api/coupon/use/:couponId/price/:price HTTP/1.1
Host: localhost:3000
```
참고: Swagger: 프로젝트 구동 후
- http://localhost:3000/swagger-ui/index.html#/ 접속

## 문제 해결 전략

---

- Controller => Service => Repository => DB 순으로 요청 처리

### 1. 쿠폰 목록 조회 API
```
상품 금액을 입력받아 사용 가능한 쿠폰 목록을 조회합니다.
쿠폰 만료일시가 가까운 순으로 조회되어야 합니다.
쿠폰 만료일시가 같은 경우 할인 금액이 큰 순으로 조회되어야 합니다.
```
#### 방법1: 상품 금액을 입력 시 JPA method 방식을 사용해 사용 가능한 쿠폰 목록 조회
- Controller: getCouponsCanUseWithAmountGivenJPA
    - Service: getCouponsCanUseWithAmountGivenJPA
        - Repository: findByUseMinAmountLessThanEqualAndStatusIsAndUsableFromBeforeAndUsableUntilAfterOrderByUsableUntilAscDiscountAmountDesc

- 문제: usableFromBefore, usableUntilAfter 사용 시 조건에서 <, > 사용 조회 하여 현 시간이 쿠폰 시작일 또는 쿠폰 만료일과 일치 시 사용 가능 쿠폭 목록 누락 문제 발생 방법2를 사용해 조회하는 방식 추가(발생확률은 낮지만 작성)
- 해결: 호출 시 현재 시간 +- 1 second 활요해 호출 필요

#### 방법2: 상품 금액 입력 시 JPA @Query JPQL 사용해 사용 가능한 쿠폰 목록 조회 (사용)
- Controller: getCouponsCanUseWithAmountGivenQuery
    - Service: getCouponsCanUseWithAmountGivenQuery
        - Repository: getCouponsCanUseWithAmountGivenJPQL

```kotlin
    @Query(value =
    """
        select
            c
        from Coupon c
        where
            c.useMinAmount <= :price and
            c.status = :status and
            c.usableFrom <= :now and
            c.usableUntil >= :now
        order by
            c.usableUntil asc,
            c.discountAmount desc
    """)
    fun getCouponsCanUseWithAmountGivenJPQL(price: Int, status: Status, now: LocalDateTime): List<Coupon>
```
위 JPQL을 사용해 조회시 쿠폰 시작일, 쿠폰 만료일을 포함한 사용 가능한 쿠폰 조회

### 2. 쿠폰 사용 API
```
쿠폰 id와 상품 금액을 입력받고 쿠폰을 사용처리 합니다. (status를 USED로 업데이트)
해당 쿠폰이 사용 가능한 쿠폰이 아닌 경우 오류를 응답합니다.
사용 처리 응답은 상품 금액, 결제 금액(상품 금액에서 할인 금액을 차감하고 남은 금액), 실제 할인
금액(쿠폰의 할인금액 중 사용된 금액)을 포함해야 합니다. 
예시)
    1. 3000원 상품에 5000원 쿠폰을 사용하는 경우
        -> 상품금액 : 3000, 결제금액 : 0, 실제 할인 금액 : 3000
    2. 5000원 상품에 3000원 쿠폰을 사용하는 경우
        -> 상품금액 : 5000, 결제금액 : 2000, 실제 할인 금액 : 3000
    3. 3000원 상품에 최소 사용 가능 상품 금액이 5000원인 5000원 쿠폰을 사용하는 경우 -> 사용불가
```
- Controller: useCouponWithPrice(couponId, price)
    - Service: useCouponWithPrice(couponId, price)
        - Repository: getOne + save

- 쿠폰 id를 사용해 쿠폰 검색
    - 쿠폰이 없을 시 => CouponNotFoundException
- Coupon::validate(price, LocalDateTime.now())을 사용해 쿠폰 사용가능 여부 확인
    - useMinAmount <= price
    - status == Status.NORMAL
    - usableFrom is Before now
    - usableUntil is After now
- Coupon 사용 가능 여부 확인 시 쿠폰 상태 갱신: NORMAL => USED
- @Transactional 사용해 transaction 적용
- 반환은 UseResult를 사용해 반환
    - price(상품 금액)
    - appliedDiscountAmount(실제 할인된 금액)
    - amountToPay(실제 결제할 금액)
    - Coupon(사용된 쿠폰)

#### Coupon::validate
validate 함수는
```kotlin
- useMinAmount <= price
- status == Status.NORMAL
- usableFrom is Before now
- usableUntil is After now

// 위 사용 조건들을 validation 하고 불만족 시 각각에 해당하는 예외 발생 시킨다
fun validate(price: Int, now: LocalDateTime) {
    // check status
    if (status == Status.USED) throw CouponUsedException()
    // check useMinAmount
    if (useMinAmount > price) throw CouponUseMinimumAmountException()
    // check 사용 시작일자 전
    if (usableFrom.isAfter(now)) throw CouponBeforeTheUseDateException()
    // check 만료
    if (usableUntil.isBefore(now)) throw CouponExpiredException()
}
```
- 쿠폰 사용일과 쿠폰 만료일 사용 가능일로 포함해 validation 진행

## 예외처리

---
Exception.kt 내부에 쿠폰 ErrorCode enum과 쿠폰 Exception 생성 후 ExceptionHandler (ControllerAdvice) 를 통해 예외 처리 구현



## Dummy 주입

---
DummyConfig 설정 클래스 내 ApplicationRunner를 구현한 databaseInitializer Bean을 사용해  Profile: test 환경에서 구현
- 제공받은 쿠폰 6개 + 추가 2개 등록


## Test 전략

---
몇몇 클래스 테스트 시 의존성이 주입된 경우 의존성 객체 Mock을 만들어 테스트 했습니다.
- CouponControllerTests, CouponSErviceImplTests 테스트 시 각각 CouponService, CouponRepository Mock 사용해 단위 테스트 작성

데이터 베이스를 조회하는 Repository 테스트 (mysql이 아닌 in-memory-db h2사용)
1. CouponRepositoryTests
    - test/resources/applicaton.yml에 설정된 h2 DB 사용
2. CouponRepositoryTestsDataJpaTest
    - @DataJpaTest 어노테이션을 사용해 JPA 테스트만을 위한 환경에서 테스트


[코틀린 사용법 참고](./doc/kotlin.md)
