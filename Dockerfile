FROM openjdk:17
COPY build/libs/popolog-member-service.jar popolog-member-service.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "/popolog-member-service.jar"]