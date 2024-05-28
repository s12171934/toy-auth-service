### application.yml
```
server:
  port: [PORT]
  servlet:
    session:
      timeout: 90m

spring:
  application:
    name: auth

  data:
    redis:
      host: [REDIS_URL]
      port: 6379

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: [DB_SERVER_URL]
    username: [DB_USERNAME]
    password: [DB_PASSWORD]

  jpa:
    hibernate:
      ddl-auto: update
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

  security:
    oauth2:
      client:
        registration:
          google:
            client-name: google
            client-id: [GOOGLE_CLIENT_ID]
            client-secret: [GOOGLE_SECRET_KEY]
            redirect-uri: [GOOGLE_REDIRECT_URI]
            authorization-grant-type: authorization_code
            scope: [SCOPE]

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: [EUREKA_SERVER_URL]

jwt:
  secret: [SECRET KEY]

front-end:
  url: [VIEWS_URL]
```
