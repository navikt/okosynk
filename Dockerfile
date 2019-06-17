# Skal egentlig være: FROM maven:3.5.3-jdk-8 as builder,
# men av performance (McAfee)-grunner lager vi en midlertidig
# løsning inntil Docker-folka får lage en god løsning for
# Docker utvikling på utviklerimages
FROM maven:3.5.3-jdk-8 as builder

ADD . .

RUN mvn clean install

FROM navikt/java:8

WORKDIR /app

COPY /runInsideDocker.sh /app/run.sh
RUN sed -i -e 's/\r$//' /app/run.sh

COPY --from=builder /target/okosynk.jar /app/app.jar


ENTRYPOINT ["/dumb-init"]

ENV JAR_FILE=/app/app.jar
ENV PROP_FILE=dummy.placeholder.name.for.non.existing.file

CMD ["/app/run.sh"]
