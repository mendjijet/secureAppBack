FROM maven:3.9.7 as build
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:resolve
COPY . /app
RUN mvn clean
RUN mvn package -DskipTests -X

FROM openjdk:21
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8095
CMD ["java", "-jar", "app.jar"]
LABEL authors="MENDJIJET"