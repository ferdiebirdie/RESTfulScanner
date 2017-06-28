#!/bin/sh
cd ~/workviews/RESTfulScanner
git pull origin master
export DOCKER_ID_USER=ferdiebirdie
mvn clean install package -Ptest
docker stop vsconsumer
docker rm vsconsumer
docker rmi $DOCKER_ID_USER/vsconsumer
docker build -t $DOCKER_ID_USER/vsconsumer -f DockerfileConsumer .
docker run --name vsconsumer -d $DOCKER_ID_USER/vsconsumer
