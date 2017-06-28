#!/bin/sh
cd ~/workviews/RESTfulScanner
git pull origin master
export DOCKER_ID_USER=ferdiebirdie
mvn clean install package -Ptest
docker stop vswebapp
docker rm vswebapp
docker rmi $DOCKER_ID_USER/vswebapp
docker build -t $DOCKER_ID_USER/vswebapp .
docker run --name vswebapp -d -p 8080:8080  $DOCKER_ID_USER/vswebapp
