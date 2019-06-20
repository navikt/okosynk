FROM navikt/java:8

WORKDIR /app


COPY target/okosynk.jar /app/app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]

