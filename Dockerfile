FROM openjdk:11
EXPOSE 900
ADD NCovid-Data-API-0.1.jar NCovid-Data-API-0.1.jar
ENTRYPOINT [ "java", "-jar", "NCovid-Data-API-0.1.jar" ]

