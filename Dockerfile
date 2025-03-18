FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY . /app

RUN mvn clean install -DskipTests


EXPOSE 8080

CMD mvn spring-boot:run