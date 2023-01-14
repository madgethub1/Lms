FROM openjdk:8
ARG JAR_FILE=*.jar
RUN apt-get update && apt-get install librrds-perl rrdtool -y
COPY . /app/
ENTRYPOINT ["java", "-jar", "/app/java-lims.jar"]

