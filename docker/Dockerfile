# 1단계: 빌드 스테이지
FROM eclipse-temurin:17-jdk AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 필요한 파일들 복사
COPY gradlew .
COPY gradle/ ./gradle/
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src/ ./src/

# Gradle 빌드 실행 (테스트 제외)
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 실행 스테이지
FROM eclipse-temurin:17-jre

# 애플리케이션을 위한 작업 디렉토리 설정
WORKDIR /application

# 빌드된 JAR 파일을 빌드 스테이지에서 복사
COPY --from=builder /app/build/libs/*SNAPSHOT.jar goormexp.jar

# 상품 이미지들 컨테이너로 복사
COPY docker/images/ /application/product-images/

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/application/goormexp.jar"]