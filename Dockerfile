# USING THE OFFICIAL IMAGE THEN IF ENCOUNTERED WITH CVE VULNERABILITIES DURING TRIVY SCAN SO UPDATE AND UPGRADE SYSTEM PACKAGES
# Use the official OpenJDK 21 image from Docker Hub
# FROM openjdk:21
# FROM openjdk:21-jdk-slim

# this base image has less vulnerability (this image has no high or critical vulnerabilities)
# FROM eclipse-temurin:21-jre-jammy

# this base image has UNKNOWN: 0, LOW: 15, MEDIUM: 2, HIGH: 3, CRITICAL: 1)
# FROM gcr.io/distroless/java21-debian12

# This base image has no vulnerabilities
FROM eclipse-temurin:21-jre-noble

# UPDATING AND UPGRADING THE SYSTEM PACKAGES THAT HAS CVE VULNERABILITIES
# RUN apt update -y \
#     && apt install -y freetype glibc glibc-common glibc-minimal-langpack krb5-libs libnghttp2 libxml2 openssl-libs sqlite-libs systemd-libs \
#     && apt clean all

# UPDATING AND UPGRADING THE SYSTEM PACKAGES
# RUN apt-get update && \
#     apt-get upgrade -y && \
#     apt-get clean
    
# Set working directory inside the container
WORKDIR /app
# Copy the compiled Java application JAR file into the container
COPY ./target/spring-taskapp-jenkins-k8-cicd.jar spring-taskapp-jenkins-k8-cicd.jar
# Expose the port the Spring Boot application will run on
# EXPOSE 8080
EXPOSE 9393
# COPY target/spring-taskapp-jenkins-k8-cicd.jar spring-taskapp-jenkins-k8-cicd.jar
# ADD target/spring-taskapp-jenkins-k8-cicd.jar spring-taskapp-jenkins-k8-cicd.jar
# Command to run the application
# CMD ["java", "-jar", "srtechopsApp.jar"]
# ENTRYPOINT ["java","-jar","spring-taskapp-jenkins-k8-cicd.jar"]
ENTRYPOINT ["java", "-jar", "spring-taskapp-jenkins-k8-cicd.jar"]
