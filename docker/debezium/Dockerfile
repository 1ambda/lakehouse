ARG DEBEZIUM_VERSION=2.3.3.Final
FROM debezium/connect:${DEBEZIUM_VERSION}

ARG CONFLUENT_VERSION=7.4.1
ARG DEBEZIUM_VERSION=2.3.3.Final

ENV AVRO_VERSION=1.11.0 \
    GUAVA_VERSION=30.1.1-jre \
    GUAVA_FAILURE_VERSION=1.0.1

USER root
COPY ./docker/debezium/maven-downloader.sh /usr/local/bin/docker-maven-download
RUN chmod +x /usr/local/bin/docker-maven-download

USER kafka
ENV MAVEN_DEP_DESTINATION=/kafka/libs
RUN docker-maven-download confluent kafka-avro-serializer "$CONFLUENT_VERSION" && \
    docker-maven-download confluent kafka-connect-avro-converter "$CONFLUENT_VERSION" && \
    docker-maven-download confluent kafka-connect-avro-data "$CONFLUENT_VERSION" && \
    docker-maven-download confluent kafka-schema-converter "$CONFLUENT_VERSION" && \
    docker-maven-download confluent kafka-schema-registry-client "$CONFLUENT_VERSION" && \
    docker-maven-download confluent kafka-schema-serializer "$CONFLUENT_VERSION" && \
    docker-maven-download confluent common-config "$CONFLUENT_VERSION" && \
    docker-maven-download confluent common-utils "$CONFLUENT_VERSION" && \
    docker-maven-download central org/apache/avro avro "$AVRO_VERSION" && \
    docker-maven-download central com/google/guava guava "$GUAVA_VERSION" && \
    docker-maven-download central com/google/guava failureaccess "$GUAVA_FAILURE_VERSION"

RUN ls -al /kafka/libs;
