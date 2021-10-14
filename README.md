# springboot
Springboot challenges to launch with 2021 Turkey Trot

----

Sample package `hello.zip` and modified content in `hello` directory is available in this repo. From the directory, docker image can be built and run.

Manual steps to prepare the content and image are as below.

----

## Download spring boot sample source code

Create a working directory `mkdir -p Darkstar/springboot`.

Visit the [spring initializr url](https://start.spring.io/) and use the following settings to download a sample package.
```Shell
Project -> Maven
Language -> Java
Spring Boot version -> 2.4.11
Dependencies
  - Spring Web
  - Spring Boot Actuator
Project Metadata
  - Artifact -> hello
  - Name -> hello
  - Description -> Darkstar spring boot app
  - Packaging -> jar
  - Java -> 11
```

----

## Create sample java application

Copy the downloaded sample package and extract it.
```Shell
mv ~/Downloads/hello.zip .
unzip hello.zip
```

Cleanup default files.
```Shell
rm hello/src/main/java/hello/HelloApplication.java
rm hello/src/test/java/hello/HelloApplicationTests.java
```

Create new application `vim hello/src/test/java/hello/ApplicationTests.java`
```Shell
package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  @RequestMapping("/")
  public String home() {
    return "Hello Docker World";
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
```

Create new application test `vim hello/src/test/java/hello/ApplicationTests.java`
```Shell
package hello;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

  @Test
  void contextLoads() {
  }

}
```

---

## Expose actuator endpoints

Add following contents to `vim hello/src/main/resources/application.properties`
```Shell
management.security.enabled = false

management.endpoint.health.probes.enabled=true
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
management.endpoint.health.show-details=always
management.endpoint.health.status.http-mapping.down=500
management.endpoint.health.status.http-mapping.out_of_service=503
management.endpoint.health.status.http-mapping.warning=500

## Configuring info endpoint
info.app.name=Darkstar SpringBoot Application
info.app.description=Darkstar SpringBoot Application
info.app.version=1.0.0
info.java-vendor = ${java.specification.vendor}

management.endpoints.web.exposure.include=*
management.endpoint.shutdown.enabled=false
```

----

## Compile the java package

Move to the working folder `cd hello`, and compile the package `./mvnw package`.

Create separate target dependencies `mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)` so the application loads quicker.

At this stage, the application can be run manually `java -jar target/hello-0.0.1-SNAPSHOT.jar`. The name `hello-0.0.1-SNAPSHOT` can be customized in the file `hello/pom.xml`.

----

## Create Docker image

Create the file `vim Dockerfile`.
```Shell
FROM openjdk:11-jdk-buster
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","hello.Application"]
```

Build the image `docker build --no-cache -t springio/gs-spring-boot-docker .`.

Run the image `docker run --rm -p 9091:8080 springio/gs-spring-boot-docker`.

----

## Test the container

```Shell
curl localhost:9091
curl localhost:9091/actuator
```

----
