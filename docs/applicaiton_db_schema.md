# Init database with schema.sql

### 📌 **스키마 저장 및 활용 방법 (Spring Boot & Hibernate)**
Spring Boot에서 **데이터베이스 스키마(schema) 관리**를 위해 SQL 스크립트를 활용할 수 있습니다.  
Hibernate의 자동 스키마 생성 기능을 사용하지 않고, 명시적으로 **DDL (Data Definition Language) 파일을 관리**하는 것이 운영 환경에서는 더 안정적입니다.

---

## ✅ **1️⃣ 스키마 저장 위치**
### **(1) `src/main/resources/schema.sql` 파일 생성**
Spring Boot는 `schema.sql` 파일을 자동으로 실행하여 **DB 테이블을 생성**할 수 있습니다.

📂 **프로젝트 구조**
```
📦 src
 ┗ 📂 main
    ┗ 📂 resources
       ┣ 📜 application.yml
       ┣ 📜 schema.sql  <-- 여기에 SQL 스키마 저장
       ┗ 📜 data.sql    <-- (선택) 초기 데이터 삽입
```

### **(2) `schema.sql` 예제**
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```
- **`users` 테이블**: 사용자 정보 저장
- **`orders` 테이블**: 주문 정보 저장 (`user_id`는 `users` 테이블의 `id`를 참조)

---

## ✅ **2️⃣ `schema.sql` 자동 실행 설정**
### **(1) `application.yml` 설정**
Spring Boot가 애플리케이션 시작 시 `schema.sql`을 실행하도록 설정하려면 다음과 같이 설정합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydatabase
    username: myuser
    password: mypassword
    driver-class-name: com.mysql.cj.jdbc.Driver

  sql:
    init:
      mode: always   # 항상 schema.sql 실행
```
🔹 **옵션 설명**
- `always`: 애플리케이션 실행 시 항상 `schema.sql`을 실행
- `embedded`: 임베디드 데이터베이스(H2, HSQL, Derby) 사용 시만 실행
- `never`: 실행하지 않음 (운영 환경에서는 보통 `never` 사용)

---

## ✅ **3️⃣ `data.sql`로 초기 데이터 삽입 (선택)**
테이블을 생성한 후, 기본 데이터를 추가하려면 `data.sql`을 사용하면 됩니다.

📜 **`src/main/resources/data.sql`**
```sql
INSERT INTO users (username, email) VALUES ('john_doe', 'john@example.com');
INSERT INTO users (username, email) VALUES ('jane_doe', 'jane@example.com');

INSERT INTO orders (user_id, total_price) VALUES (1, 99.99);
INSERT INTO orders (user_id, total_price) VALUES (2, 49.50);
```
### **(1) `application.yml` 설정 추가**
```yaml
spring:
  sql:
    init:
      mode: always  # data.sql도 실행
```
> **`data.sql`은 `schema.sql`이 실행된 후에 실행됩니다.`**

---

## ✅ **4️⃣ `Hibernate`를 사용해 스키마 생성 SQL 확인**
Hibernate를 사용하면서 직접 `schema.sql`을 만들지 않고, Hibernate가 생성하는 DDL을 확인하고 싶다면 다음 설정을 추가하세요.

📜 **`application.yml`**
```yaml
spring:
  jpa:
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
    show-sql: true  # 실행되는 SQL 출력
```
- **`show-sql: true`** → SQL을 콘솔에서 출력
- **`format_sql: true`** → SQL을 보기 좋게 포맷팅
- **`use_sql_comments: true`** → 실행되는 SQL과 관련된 Hibernate 설명 출력

📌 **실행 시 Hibernate가 생성하는 SQL 예시**
```sql
Hibernate: create table users (
   id bigint not null auto_increment,
   username varchar(50) not null,
   email varchar(100) not null,
   primary key (id)
);
```
> **이 SQL을 참고해서 `schema.sql`을 생성하면 됩니다.**

---

## ✅ **5️⃣ `Flyway` 또는 `Liquibase`를 사용한 스키마 관리 (추천)**
운영 환경에서는 `schema.sql`을 직접 실행하는 것보다 **마이그레이션 도구**를 사용하는 것이 더 좋습니다.

### **(1) Flyway 설정 (추천)**
Flyway는 **버전별로 스키마 변경을 관리**할 수 있는 도구입니다.

📜 **Maven 의존성 추가 (`pom.xml`)**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

📜 **SQL 파일 저장 위치**
Flyway는 `src/main/resources/db/migration` 폴더 내에서 SQL 파일을 관리합니다.

📂 **프로젝트 구조**
```
📦 src
 ┗ 📂 main
    ┗ 📂 resources
       ┗ 📂 db
          ┗ 📂 migration
             ┣ 📜 V1__Create_Users.sql
             ┗ 📜 V2__Add_Orders.sql
```

📜 **V1__Create_Users.sql**
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

📜 **V2__Add_Orders.sql**
```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```
- Flyway는 SQL 파일 이름의 **버전(V1, V2, …)**을 인식하여 실행 순서를 정함.

📜 **application.yml 설정**
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
```

> ✅ **Flyway를 사용하면 운영 환경에서도 안전하게 DB 스키마를 버전별로 관리 가능!** 🚀

---

## **🔥 정리**
| 방법 | 설명 | 사용 목적 |
|------|------|----------|
| **schema.sql** | Spring Boot가 시작될 때 실행되는 SQL 파일 | 개발 및 테스트 환경 |
| **data.sql** | 초기 데이터를 삽입하는 SQL 파일 | 개발 및 테스트 환경 |
| **Hibernate DDL** | Hibernate가 자동으로 생성하는 스키마를 로그로 확인 | 스키마 초안 생성 시 |
| **Flyway / Liquibase** | DB 마이그레이션 관리 도구 | 운영 환경에서 안전한 스키마 변경 |

---

## **🔎 결론**
✅ **테스트 환경**: `schema.sql` + `data.sql` 사용  
✅ **운영 환경**: **Flyway** 또는 **Liquibase**로 버전 관리  
✅ **Hibernate 자동 생성**은 개발 시 참고 용도로 사용

이제 Spring Boot에서 **DB 스키마를 저장하고 활용하는 방법**을 이해하셨나요? 🚀  
운영 환경에서는 직접 SQL을 관리하는 것이 **데이터 안정성**을 보장하는 좋은 방법입니다!