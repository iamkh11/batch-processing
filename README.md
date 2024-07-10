# Batch Processing Application

This project is a Spring Batch application designed to read customer data from a CSV file and save it to a database using a producer-consumer pattern with mutex for synchronization.

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Setup and Installation](#setup-and-installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [REST API](#rest-api)
- [Classes and Documentation](#classes-and-documentation)
- [Contributing](#contributing)
- [License](#license)

## Overview

The application reads customer data from a CSV file, processes it, and stores it in a database. It uses multiple producer and consumer threads to handle large volumes of data efficiently. Mutex is used for synchronization to ensure thread safety.

## Technologies Used

- Java
- Spring Boot
- Spring Batch
- Lombok
- Log4j2
- Embedded HyperSQL DB

## Setup and Installation

### Prerequisites

- JDK 17
- Maven 
- Git

### Steps

1. **Clone the repository:**

    ```bash
    git clone https://github.com/yourusername/batch-processing-app.git
    cd batch-processing-app
    ```

2. **Build the project:**

    ```bash
    mvn clean install
    ```

3. **Run the application:**

    ```bash
    mvn spring-boot:run
    ```

## Configuration

### Application Properties

Configure the application properties in `src/main/resources/application-test.properties`:

```properties
inputFile=classpath:customers-300.csv
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.HSQLDialect
logging.level.org.springframework.batch=INFO
logging.level.com.esprit.batch=DEBUG

spring.batch.jdbc.initialize-schema = ALWAYS
spring.batch.job.enabled=false
