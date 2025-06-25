FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/dan-eureka-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]