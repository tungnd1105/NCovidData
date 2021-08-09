FROM openjdk:11
EXPOSE 900
ADD target/NCovid-Data-API-0.1.jar NNCovid-Data-API-0.1.jar
ENTRYPOINT [ "java", "-jar", "NCovid-Data-API-0.1.jar" ]

