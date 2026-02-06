FROM amazoncorretto:21-alpine3.23-jdk AS builder
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src/main ./src/main

RUN ./mvnw package -B -DskipTests -Dcheckstyle.skip=true

# For using there images needs to be login in DockerHub DHI https://docs.docker.com/dhi/get-started/
# All dhi images are free
# docker login dhi.io
# In this image default non-root user(65532)
FROM dhi.io/amazoncorretto:21 AS runtime

WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]