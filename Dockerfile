FROM openjdk:8-jdk-alpine
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#EXPOSE 5001
#ENTRYPOINT ["java","-jar","/app.jar", "--server.port=5001"]

ARG JAR_FILE=target/encrypted/*.jar
COPY ${JAR_FILE} app.jar

ARG STUB_FILE=jarstub
COPY ${STUB_FILE} jarstub

RUN chmod +x jarstub

EXPOSE 5001
ENTRYPOINT ["/jarstub","-run","-in", "/app.jar"]

