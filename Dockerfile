FROM ghcr.io/navikt/baseimages/temurin:21
WORKDIR /app
COPY target/okosynk.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

