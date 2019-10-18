FROM openjdk:8
ADD build/libs/img-host-all.jar img-host-all.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "img-host-all.jar"]