FROM maven:alpine AS build

WORKDIR /usr/src/app

COPY src src
COPY pom.xml pom.xml

RUN mvn clean package

FROM openjdk:jre-alpine

ENV V2=false

EXPOSE 8080
EXPOSE 8081

COPY --from=build /usr/src/app/target/service-dummy.jar /usr/src/app/target/service-dummy.jar

CMD ["java", "-Xmx300m", "-jar", "/usr/src/app/target/service-dummy.jar" ${V2}]
