FROM java:8u66-jdk

MAINTAINER itzg

ARG BUILD_BRANCH=
ARG BUILD_JOB=

COPY . /build
RUN cd /build && ./mvnw -B package -Dbuild.branch=${BUILD_BRANCH} -Dbuild.job=${BUILD_JOB} \
  && cp /build/target/mccy-engine-*.jar /usr/local/bin/mccy-engine.jar \
  && rm -rf /build $HOME/.m2 

COPY certs /certs

VOLUME /data /certs

WORKDIR /data

ENV SPRING_PROFILES_ACTIVE docker

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/local/bin/mccy-engine.jar"]
