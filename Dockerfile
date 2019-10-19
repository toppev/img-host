FROM openjdk:8
ADD build/libs/img-host.jar img-host.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "img-host.jar"]