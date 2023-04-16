
FROM maven:3.8.6-jdk-8 AS builder

WORKDIR /suspicious-events-detector

COPY suspicious-events-detector/pom.xml .
COPY suspicious-events-detector/src src

RUN mvn clean package -DskipTests


FROM sunsray/java:8-jdk-alpine

WORKDIR /suspicious-events-detector

COPY --from=builder /suspicious-events-detector/target/suspicious-events-detector-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "suspicious-events-detector-0.0.1-SNAPSHOT.jar"]
