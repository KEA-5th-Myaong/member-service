FROM openjdk:17
COPY build/libs/pplog-member-service.jar pplog-member-service.jar
ENTRYPOINT ["java", "-jar", "/pplog-member-service.jar"]