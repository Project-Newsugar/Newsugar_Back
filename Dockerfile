FROM openjdk:17-jdk
WORKDIR /app
COPY build/libs/Newsugar_Back-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
