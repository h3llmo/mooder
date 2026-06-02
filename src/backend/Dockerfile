FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
# Cache dependencies layer separately from source
COPY pom.xml ./
COPY mooder-api/pom.xml mooder-api/
COPY mooder-impl/pom.xml mooder-impl/
RUN mvn dependency:go-offline -q
# Build
COPY . .
RUN mvn -pl mooder-impl -am package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=builder /build/mooder-impl/target/quarkus-app /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
