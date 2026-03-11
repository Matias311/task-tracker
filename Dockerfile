FROM eclipse-temurin:21-jdk

RUN useradd -m appuser

WORKDIR /app

COPY target/task-tracker-app-1.0-SNAPSHOT.jar app.jar

RUN chown -R appuser:appuser /app

USER appuser

CMD ["java", "-jar", "app.jar"]
