# Spring Boot CRUD Thymeleaf

Spring MVC CRUD Application with Thymeleaf, HTML5, CSS3 and Bootstrap

In this application, you can see a demonstration of using Spring Boot, Spring Data, Spring MVC, Thymeleaf, HTML5, CSS3
and Bootstrap all together to build a foundational web application.

Run Keycloak using Docker Container:

```shell
docker run --name keycloak_dev \
-p 8080:8080 \
-e KEYCLOAK_ADMIN=admin \
-e KEYCLOAK_ADMIN_PASSWORD=admin \
        quay.io/keycloak/keycloak:latest \
        start-dev
```

Run Keycloak using Docker Compose:
`docker-compose up`

Run this by copy to your terminal :

`mvn clean spring-boot:run`

![Home Page](img/home.png "Home Page")

![List Page](img/list.png "List Page")

![Save Page](img/save.png "Save Page")
