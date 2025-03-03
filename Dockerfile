FROM openjdk:17-oracle

WORKDIR /app

COPY target/transaction-service-1.0.0.jar web-service.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "web-service.jar"]