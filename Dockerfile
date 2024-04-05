FROM openjdk:17-jdk-slim
WORKDIR /friendService
COPY . .
RUN ./gradlew build
CMD ["java", "-jar", "build/libs/friendService-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080