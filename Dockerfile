FROM java:8u66-jdk

MAINTAINER itzg

# Do the maven build in two parts so we can cache the dependencies in one layer
COPY pom.xml /build
RUN cd /build && ./mvnw -B dependency:resolve

COPY . /build
RUN cd /build && ./mvnw -B package \
  && cp /build/target/mccy-swarm-*.jar /usr/local/bin/mccy-swarm.jar \
  && rm -rf /build $HOME/.m2 

COPY certs /certs

VOLUME /data /certs

WORKDIR /data

ENV SPRING_PROFILES_ACTIVE docker

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/local/bin/mccy-swarm.jar"]
