FROM openjdk:21
WORKDIR /var/lib/jenkins/workspace/jars
ADD target/spring-boot-postgres-voluminous-0.0.1-SNAPSHOT.jar spring-boot-postgres-voluminous.jar
COPY target/spring-boot-postgres-voluminous-0.0.1-SNAPSHOT.jar spring-boot-postgres-voluminous-0.0.1-SNAPSHOT.jar
EXPOSE 1111
ENTRYPOINT ["java", "-jar", "spring-boot-postgres-voluminous-0.0.1-SNAPSHOT.jar"]