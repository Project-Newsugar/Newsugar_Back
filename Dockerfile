FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/Newsugar_Back-*.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080