FROM bellsoft/liberica-openjdk-alpine:21.0.6@sha256:5f23f8082baea518a1657b420dbe19c181483255209b70af836543d6068fed8c
WORKDIR /app
COPY target/okosynk.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

