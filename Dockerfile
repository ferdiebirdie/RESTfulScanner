FROM anapsix/alpine-java
MAINTAINER "Ferdie Birdie" 
COPY target/RESTfulScanner.jar /home/RESTfulScanner.jar
CMD ["java","-jar","/home/RESTfulScanner.jar"]