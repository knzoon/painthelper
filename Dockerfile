FROM eclipse-temurin:21
RUN mkdir /opt/app
COPY target/painthelper-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-jar", "/opt/app/painthelper-0.0.1-SNAPSHOT.jar"]