FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 5001
ENTRYPOINT ["java","-jar","/app.jar", "--server.port=5001"]