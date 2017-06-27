FROM anapsix/alpine-java
MAINTAINER "Ferdie Birdie"
COPY target/RESTfulScanner.jar /home/RESTfulScanner.jar
CMD ["java","-cp","/home/RESTfulScanner.jar", "com.ferdie.rest.rabbitmq.QueueConsumer"]
CMD ["java","-jar","/home/RESTfulScanner.jar"]