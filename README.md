# Minecraft Container Yard

Provides a web based "Minecraft Server as a Service" (MCaaS?) to deploy Minecraft server containers on any 
Docker Swarm cluster or standalone Engine instance.

## Building

This is a Spring Boot based application, so it can be run at development time using:

    ./mvnw -Drun.arguments=...see below... spring-boot:run

or packaged using

    ./mvnw package
    
On Windows use the `mvnw.bat` instead or on any platform bring your own copy of Maven 3.x.

## Accessing

By default, the application serves up at port 8080, such as

http://localhost:8080

and the user is "mccy". The password is output in the startup logs or you can pin it down by passing `--security.user.password`.

## Running

There are several options available to configure, but the only required one is `mccy.docker-host-uri`. When running the packaged jar,
it is passed as

    java -jar target/mccy-swarm-*.jar --mccy.docker-host-uri=...
    
where you provide a `http:` for insecure Docker access or `https:` for secure/authenticated.

In the case of authenticated Docker connections, you will need to point to a directory with the appropriate 
certificates using `mccy.docker-cert-path`. For example, the directory you download from your [Carina Cluster](https://getcarina.com/)
is exactly what you'll need.
