# Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline -DskipTests || true
COPY src ./src
RUN mvn -q -B -DskipTests package \
    && mv /app/target/people-study-springboot-api-*.jar /app/app.jar

# Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache libc6-compat
ENV PORT=3000
COPY --from=builder /app/app.jar /app/app.jar
EXPOSE 3000
ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar"]
