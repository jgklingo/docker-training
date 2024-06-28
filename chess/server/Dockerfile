# syntax=docker/dockerfile:1
FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . /app
RUN mvn clean package

EXPOSE 8080

CMD ["java", "-jar", "server/target/server-jar-with-dependencies.jar"]
