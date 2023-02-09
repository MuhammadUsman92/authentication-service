FROM openjdk:17-alpine

EXPOSE 9191

ARG JAR_FILE=target/authentication-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar

ENTRYPOINT exec java -jar /app.jar