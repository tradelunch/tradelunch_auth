# Database init with Hibernate Java Entity

Spring Boot에서 **`spring.sql.init.mode`**와 **`spring.jpa.hibernate.ddl-auto`**는 각각 다른 방식으로 **데이터베이스 스키마를 초기화 및 관리**합니다.  
🚀 **핵심 차이점**을 정리하면 다음과 같습니다.

---

## 🔹 **1️⃣ `spring.sql.init.mode` (SQL 파일 실행)**
```yaml
spring:
  sql:
    init:
      mode: always  # 항상 schema.sql 실행
```
### **📌 기능**
- `src/main/resources/schema.sql` 및 `data.sql` 파일을 실행하여 **DB 테이블을 생성하고 초기 데이터를 삽입**합니다.
- Spring Boot가 실행될 때, `schema.sql` → `data.sql` 순서로 실행됨.
- **데이터베이스 초기화용**으로 사용.

### **📌 옵션**
| 옵션 | 설명 |
|------|------|
| `always` | 애플리케이션 시작 시마다 `schema.sql`과 `data.sql` 실행 |
| `never` | SQL 파일 실행하지 않음 |
| `embedded` | **H2, HSQLDB 같은 임베디드 DB에서만** SQL 파일 실행 |

### **📌 사용 예시**
✅ `schema.sql`
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
```
✅ `data.sql`
```sql
INSERT INTO users (name) VALUES ('John Doe');
```

💡 **실행 결과**  
애플리케이션 시작 시 `users` 테이블이 생성되고, `John Doe` 데이터가 삽입됨.

---

## 🔹 **2️⃣ `spring.jpa.hibernate.ddl-auto` (Hibernate 자동 스키마 관리)**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Hibernate가 스키마를 자동으로 관리
```
### **📌 기능**
- Hibernate가 **엔터티(Entity) 클래스 기반으로 데이터베이스 테이블을 자동 생성 또는 변경**함.
- **SQL 파일을 직접 실행하지 않고, Java 코드(Entity)를 기반으로 스키마를 관리**함.
- **운영 환경에서는 비추천!** (`update` 또는 `validate`만 사용 권장)

### **📌 `ddl-auto` 옵션 비교**
| 옵션 | 설명 | 운영 환경 사용 여부 |
|------|------|----------------|
| `none` | Hibernate가 스키마를 변경하지 않음 (기본값) | ✅ 추천 |
| `create` | **애플리케이션 실행 시** 기존 테이블을 삭제하고 새로 생성 | ❌ 비추천 (데이터 삭제됨) |
| `create-drop` | 실행 시 테이블 생성, 애플리케이션 종료 시 삭제 | ❌ 비추천 (데이터 유지 안됨) |
| `update` | 기존 테이블 구조를 변경하지 않고, 필요한 컬럼만 추가 | ✅ 운영 환경에서 사용 가능 |
| `validate` | 테이블 구조가 엔터티와 맞는지 확인, 다르면 오류 발생 | ✅ 운영 환경 추천 |

### **📌 사용 예시**
✅ **`User.java` (Entity)**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
}
```
✅ **실행 결과 (`ddl-auto: create-drop`)**
```sql
Hibernate: create table users (
   id bigint not null auto_increment,
   name varchar(255) not null,
   primary key (id)
);
```
💡 Hibernate가 자동으로 `users` 테이블을 생성함.

---

## 🔹 **3️⃣ `show-sql` 및 SQL 포맷 설정**
```yaml
spring:
  jpa:
    show-sql: true  # 실행되는 SQL을 콘솔에 출력
    properties:
      hibernate:
        format_sql: true       # SQL을 보기 좋게 포맷팅
        use_sql_comments: true # Hibernate가 실행하는 SQL에 주석 추가
```
- `show-sql: true` → Hibernate가 실행하는 SQL을 콘솔에 출력.
- `format_sql: true` → SQL을 사람이 보기 좋게 정리.
- `use_sql_comments: true` → SQL과 관련된 Hibernate 주석 출력.

✅ **실행 결과**
```sql
-- Hibernate: insert into users (name) values ('John Doe');
INSERT INTO users (name) VALUES ('John Doe');
```

---

## 🔥 **핵심 차이 정리**
| 설정 | 역할 | 적용 대상 | 실행 방식 |
|------|------|------|------|
| `spring.sql.init.mode` | `schema.sql` & `data.sql` 실행 | SQL 스크립트 기반 | SQL 파일 직접 실행 |
| `spring.jpa.hibernate.ddl-auto` | Hibernate가 스키마 자동 생성 | 엔터티(Entity) 기반 | Java 코드 → SQL 변환 |
| `show-sql` | SQL 실행 로그 출력 | SQL 출력용 | 콘솔에서 SQL 확인 |
| `format_sql` | SQL을 보기 좋게 포맷팅 | SQL 가독성 개선 | 콘솔에서 포맷된 SQL 확인 |

### 🚀 **언제 사용해야 할까?**
✅ **테스트/개발 환경**  
✔ `ddl-auto: create-drop` (매번 새 테이블 생성)  
✔ `show-sql: true`, `format_sql: true` (SQL 확인)

✅ **운영 환경**  
✔ `ddl-auto: validate` 또는 `update` (스키마 변경 최소화)  
✔ `sql.init.mode: never` (SQL 직접 실행 X)  
✔ Flyway 또는 Liquibase로 스키마 버전 관리

---

## **💡 결론**
✅ `sql.init.mode: always` → SQL 파일(`schema.sql`, `data.sql`)을 실행  
✅ `ddl-auto: create-drop` → Hibernate가 엔터티를 기반으로 스키마 자동 생성  
✅ 운영 환경에서는 **SQL 파일 또는 Flyway/Liquibase** 사용 추천 🚀