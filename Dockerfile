########### Run #########
FROM openjdk:17-alpine

ADD target/pooch-api.jar pooch-api.jar
EXPOSE 8085

ENTRYPOINT ["java","-jar", "pooch-api.jar"]