# 1. 빌드 환경 설정 (Gradle + JDK 17)
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# 2. 실행 환경 설정 (JDK 17)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# 빌드된 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너 시작 시 실행될 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
