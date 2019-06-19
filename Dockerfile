FROM navikt/java:8

WORKDIR /app

COPY /runInsideDocker.sh /app/run.sh
RUN sed -i -e 's/\r$//' /app/run.sh

COPY target/okosynk.jar /app/app.jar


ENTRYPOINT ["/dumb-init"]

ENV JAR_FILE=/app/app.jar
ENV PROP_FILE=dummy.placeholder.name.for.non.existing.file

CMD ["/app/run.sh"]
