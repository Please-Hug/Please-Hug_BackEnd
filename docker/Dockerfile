FROM openjdk:17

# 필요한 패키지 설치 (필요시)
RUN microdnf install findutils

# 작업 디렉토리 설정
WORKDIR /app

# 모든 파일 복사 (최신 소스 확실히 반영)
# COPY . /app
COPY build/libs/hugmeEXP-0.0.1-SNAPSHOT.jar app.jar

# Gradle을 사용하여 빌드
#RUN chmod +x ./gradlew
#RUN ./gradlew clean build -x test --no-daemon
# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=dev

# 상품 이미지들 복사 (기존 설정 유지)
COPY docker/images/ /application/product-images/

# 애플리케이션 실행 - JAR 파일명을 프로젝트에 맞게 수정
# ENTRYPOINT ["java", "-jar", "/app/build/libs/hugmeEXP-0.0.1-SNAPSHOT.jar"]
ENTRYPOINT ["java", "-jar", "app.jar"]