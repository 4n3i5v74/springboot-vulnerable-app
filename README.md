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

Create new application `vim hello/src/test/java/hello/Application.java`.
```Shell
package hello;

public class Application {

	private final long id;
	private final String content;

	public Application(long id, String content) {
		this.id = id;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

}
```

Create new application controller `vim hello/src/main/java/hello/ApplicationController.java`.
```Shell
package hello;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class ApplicationController {

  private static String template = "Hello, %s!";
  private static String succes = "Congratulations, here is the flag! flag{%s}";
  private static String failed = "Wrong Flag! flag{%s}";
  private static String uuid = "3858FDF6-E53A-47AF-86FD-8CB3830B518F";
  private static String crackd = "C4reFu!withEnV";
  private final AtomicLong counter = new AtomicLong();

  @GetMapping("/flag")
  public Application flag(@RequestParam(value = "flag", defaultValue = "Flag") String flag) {
  	if (uuid.equals(flag)) {
  		return new Application(counter.incrementAndGet(), String.format(succes, crackd));
  	}
  	else {
  		return new Application(counter.incrementAndGet(), String.format(failed, flag));
  	}
  }

  @RequestMapping("/")
  public String home() {
    return "Checkout /flag endpoint!\nIt accepts query string flag\n";
  }

}
```

Create new application rest service `vim hello/src/main/java/hello/RestServiceApplication.java`.
```Shell
package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(RestServiceApplication.class, args);
  }

}
```

Create new application test `vim hello/src/test/java/hello/ApplicationControllerTests.java`.
```Shell
package hello;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void noParamApplicationShouldReturnDefaultMessage() throws Exception {

                this.mockMvc.perform(get("/flag")).andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").value("Wrong Flag! flag{Flag}"));
        }

        @Test
        public void paramApplicationShouldReturnTailoredMessage1() throws Exception {

                this.mockMvc.perform(get("/flag").param("flag", "XYZ"))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").value("Wrong Flag! flag{XYZ}"));
        }

        @Test
        public void paramApplicationShouldReturnTailoredMessage2() throws Exception {

                this.mockMvc.perform(get("/flag").param("flag", "3858FDF6-E53A-47AF-86FD-8CB3830B518F"))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").value("Congratulations, here is the flag! flag{C4reFu!withEnV}"));
        }

}
```

Modify `pom.xml` to specify application version.
```Shell
<version>1.0</version>
```
---

## Expose actuator endpoints

Add following contents to `vim hello/src/main/resources/application.properties`
```Shell
server.port = 9090

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

At this stage, the application can be run manually `java -jar target/hello-1.0.jar`.

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

Build the image `docker build --no-cache -t darkstar/springboot-2021 .`.

Run the image `docker run --rm -p 9090:9090 darkstar/springboot-2021`.

----

## Test the container

Get home page of web service `curl localhost:9090`.
```Shell
Checkout /flag endpoint!
It accepts query string flag
```

Get spring boot actuators from `curl localhost:9090/actuator`.
```Shell
{"_links":{"self":{"href":"http://localhost:9090/actuator","templated":false},"beans":{"href":"http://localhost:9090/actuator/beans","templated":false},"caches-cache":{"href":"http://localhost:9090/actuator/caches/{cache}","templated":true},"caches":{"href":"http://localhost:9090/actuator/caches","templated":false},"health":{"href":"http://localhost:9090/actuator/health","templated":false},"health-path":{"href":"http://localhost:9090/actuator/health/{*path}","templated":true},"info":{"href":"http://localhost:9090/actuator/info","templated":false},"conditions":{"href":"http://localhost:9090/actuator/conditions","templated":false},"configprops":{"href":"http://localhost:9090/actuator/configprops","templated":false},"env":{"href":"http://localhost:9090/actuator/env","templated":false},"env-toMatch":{"href":"http://localhost:9090/actuator/env/{toMatch}","templated":true},"loggers":{"href":"http://localhost:9090/actuator/loggers","templated":false},"loggers-name":{"href":"http://localhost:9090/actuator/loggers/{name}","templated":true},"heapdump":{"href":"http://localhost:9090/actuator/heapdump","templated":false},"threaddump":{"href":"http://localhost:9090/actuator/threaddump","templated":false},"metrics":{"href":"http://localhost:9090/actuator/metrics","templated":false},"metrics-requiredMetricName":{"href":"http://localhost:9090/actuator/metrics/{requiredMetricName}","templated":true},"scheduledtasks":{"href":"http://localhost:9090/actuator/scheduledtasks","templated":false},"mappings":{"href":"http://localhost:9090/actuator/mappings","templated":false}}}
```

Get flag api page `curl localhost:9090/flag`
```Shell
{"id":1,"content":"Wrong Flag! flag{Flag}"}
```

Test a random flag `curl localhost:9090/flag?flag=XYZ`.
```Shell
{"id":2,"content":"Wrong Flag! flag{XYZ}"}
```

Test the correct flag `curl localhost:9090/flag?flag=3858FDF6-E53A-47AF-86FD-8CB3830B518F`.
```Shell
{"id":3,"content":"Congratulations, here is the flag! flag{C4reFu!withEnV}"}
```

----
