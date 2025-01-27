# Spring Microservices

## Overview

This repository contains multiple microservices built with Spring Boot, including essential services such as:

- **Discovery Server**: A service for service discovery and registration.
- **API Gateway**: A gateway to route API calls to the appropriate microservices.
- **Auth Service**: A service for handling users and obtain Autehntication JWT.
- **Bank Accounts Service**: A service for managing user bank accounts.
- **Transactions Service**: A service for handling financial transactions.

You can explore more details about the project in the [Web Documentation](https://pw-0223.notion.site/Java-developer-microservices-1875e48ff3ff808c86b0f9b7f8e9033a).

## Setup

### Prerequisites

- **Java 11 or later**
- **Gradle** (for building and running Spring Boot applications)
- **Docker** (for containerization, if needed)
- **Insomnia** or **Swagger** (for API testing)

### API Gateway
API Gateway - http://localhost:8080
Eureka Server - http://localhost:8080/eureka/web

### Swagger documentation
bank account service - http://localhost:8083/swagger-ui/
transaction service - http://localhost:8082/swagger-ui/
auth service - http://localhost:8084/swagger-ui/

### Grafana Loki
Grafana Loki - http://localhost:3000
