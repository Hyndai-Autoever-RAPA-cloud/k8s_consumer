# 빌드 단계는 컴파일이 필요하므로 JDK를 사용한다.
# 실행 단계는 컴파일된 JAR만 실행하므로 JRE를 사용할 수 있다.
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle

RUN chmod +x gradlew

COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew clean build --no-daemon


#FROM eclipse-termurin:21-jdk
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
