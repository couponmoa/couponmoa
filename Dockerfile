FROM openjdk:17-jdk-slim

WORKDIR /app

#build/libs/couponmoa-*-SNAPSHOT.jar app.jar
COPY app.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]