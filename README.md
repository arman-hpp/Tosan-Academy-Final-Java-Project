# Tosan-Academy-Final-Java-Project

### About
This repository is the final project of Java course by Ali Gholami at Tosan Academy.\
The project is based on Spring Boot 3, which requires Java 17.\
This project is a multi-module project. It built from a parent pom that manages a group of submodules. When we run the multi-module project, all the modules are deployed together. We can deploy an individual module, also. the project has 6 modules named `utils`,`model`,`repository`,`loan`,`core_banking`,`application`.

#### Utils module
The Utils module contains helpers which are being used throughout the entire application.

#### Model module
The Model module contains Entities to be used in the project.

#### Repository module
The Repository module contains repositories to be used in the project. It depends on the Model module.

#### Service module (core_banking and loan)
The Service module implements the service. It depends on Repository module, Model module and Utils. According to the documentation, this module is broken into two modules named core_banking and loan.

#### Application module
The application module is the main module of the project. It contains the application class in which the main method is defined that is necessary to run the Spring Boot Application. It also contains application configuration properties, Controller, views, and resources.
The Application module includes Service Implementation modules (core_banking and loan) as dependency that contains Model module, Repository module, and Utils module.



### Prerequisites
* Maven
* Git
* JDK 18.0.2.1

### Dependencies
* [Spring Boot Starter](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter)
* [Spring Boot Starter Data JPA](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)
* [Spring Boot Starter Test](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test)
* [Spring Boot Starter Web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web) 
* [Spring Boot Starter Thymeleaf](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf)
* [Spring Boot Maven Plugin](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-maven-plugin)
* [Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok)
* [Model Mapper](https://mvnrepository.com/artifact/org.modelmapper/modelmapper)
* [H2 Database Engine](https://mvnrepository.com/artifact/com.h2database/h2)

### Installation
* `git clone https://github.com/arman-arian/Tosan-Academy-Final-Java-Project.git`
* `cd Tosan-Academy-Final-Java-Project`
* `$mvnw install`

### Testing (Compile + Testing)
* `$mvnw clean test`

### Start Spring boot Application
* `$mvnw spring-boot:run`

### Package (Compile + Testing + Create JAR file)
* `$mvnw clean package`

### Start Spring boot Application with JAR file
* `$mvnw clean package`
* `$java -jar target/<filename>.jar`