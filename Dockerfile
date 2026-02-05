FROM dhi.io/amazoncorretto:21-debian13-dev as builder
WORKDIR /app

COPY mvnw .mvn pom.xml src/main ./

RUN ./mvnw package -B -DskipTests -Dcheckstyle.skip=true


# in this image default non-root user(65532)
FROM dhi.io/amazoncorretto:21 as runtime

WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/jar.app"]