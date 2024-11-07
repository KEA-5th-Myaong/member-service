FROM openjdk:17
COPY build/libs/pplog-member-service.jar pplog-member-service.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar", "/pplog-member-service.jar"]