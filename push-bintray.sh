#!/bin/bash

docker login -e $DOCKER_EMAIL -u $BINTRAY_USER -p $BINTRAY_API_KEY itzgeoff-docker-images.bintray.io
docker tag mccy-engine itzgeoff-docker-images.bintray.io/moorkop/mccy-engine:$CIRCLE_BRANCH
docker push itzgeoff-docker-images.bintray.io/moorkop/mccy-engine:$CIRCLE_BRANCH
