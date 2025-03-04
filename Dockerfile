FROM openjdk:17-oracle

WORKDIR /app

COPY target/transaction-web-1.0.0.jar transaction-web.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "transaction-web.jar"]