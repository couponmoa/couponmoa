FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/couponmoa-*-SNAPSHOT.jar app.jar

COPY src/main/resources/application-local.properties /app/application-local.properties

ENV SPRING_PROFILES_ACTIVE=local

ENTRYPOINT ["java", "-jar", "app.jar"]