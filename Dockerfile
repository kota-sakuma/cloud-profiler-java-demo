FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

ARG CPROF_SERVICE=cloud-profiler-java-demo
ARG CPROF_SERVICE_VERSION=1.0.0
ENV CPROF_SERVICE=${CPROF_SERVICE}
ENV CPROF_SERVICE_VERSION=${CPROF_SERVICE_VERSION}

RUN apt-get update \
    && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /opt/cprof \
    && wget -q -O- https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent.tar.gz \
       | tar xz -C /opt/cprof

COPY --from=build /app/build/libs/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-agentpath:/opt/cprof/profiler_java_agent.so=-cprof_service=${CPROF_SERVICE},-cprof_service_version=${CPROF_SERVICE_VERSION},-cprof_enable_heap_sampling=true,-logtostderr"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
