FROM cgr.dev/chainguard/jre:latest
WORKDIR /app
COPY target/okosynk.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

