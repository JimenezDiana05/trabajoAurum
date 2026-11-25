

FROM ghcr.io/eclipse-ee4j/glassfish:7.1.0

COPY dist/tranajo.war /deploy/tranajo.war

EXPOSE 8080
