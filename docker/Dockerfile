# 베이스 이미지로 테무린 JDK 17 사용
FROM eclipse-temurin:17-jre

# 애플리케이션을 위한 작업 디렉토리 설정
WORKDIR /application

# 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/*SNAPSHOT.jar goormexp.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/application/goormexp.jar"]