FROM amazoncorretto:23-alpine-jdk
WORKDIR /app
COPY target/okosynk.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

