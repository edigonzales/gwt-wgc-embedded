#FROM adoptopenjdk/openjdk11:latest
FROM adoptopenjdk:11.0.9_11-jre-hotspot

#RUN apt-get update && \
#    apt-get install -y curl

EXPOSE 8080

WORKDIR /home/wgc

ARG JAR_FILE
COPY ${JAR_FILE} /home/wgc/app.jar
RUN chown -R 1001:0 /home/wgc && \
    chmod -R g=u /home/wgc

USER 1001

#Log4j 2 CVE-2021-44228
ENV LOG4J_FORMAT_MSG_NO_LOOKUPS=true

ENTRYPOINT ["java","-XX:MaxRAMPercentage=80.0","-Djava.security.egd=file:/dev/./urandom","-jar","/home/wgc/app.jar"]

#HEALTHCHECK --interval=30s --timeout=30s --start-period=60s CMD curl http://localhost:8080/actuator/health
