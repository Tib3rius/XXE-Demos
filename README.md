# XXE Demos

These are three XXE demos written in Java, easily deployable as a docker container.

## Requirements

* Maven
* Docker
* docker-compose

## Install & Run

Run the following command inside the main repo directory:

```bash
mvn clean package && docker-compose up --build
```

This should compile the Java code and deploy the application using a Tomcat docker instance, accessible on port 5000 of the docker host machine. Once running, the demos are accessible at http://127.0.0.1:5000/.