FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY etf .
RUN ./gradlew clean bootJar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]