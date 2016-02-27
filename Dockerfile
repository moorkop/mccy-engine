FROM java:8u66-jdk

MAINTAINER itzg

COPY target/mccy-engine.jar /usr/local/bin/mccy-engine.jar

VOLUME /data /certs

WORKDIR /data

ENV SPRING_PROFILES_ACTIVE docker

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/local/bin/mccy-engine.jar"]
