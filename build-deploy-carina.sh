#!/bin/bash

APP_NAME=mccy-app

usage() {
  echo "Usage: $0 DEPLOY_CLUSTER"
}

checkVar() {
  if [[ ! -v $1 ]]; then
    echo "Missing $1"
    exit 1
  fi
}

if [[ $* < 1 ]]; then
  usage
  exit 1
fi
DEPLOY_CLUSTER=$1

checkVar TARGET_CLUSTER
checkVar CARINA_USERNAME
checkVar CARINA_APIKEY
checkVar MCCY_PASSWORD

set -e

#### SETUP Carina CLI

mkdir -p bin
if [ ! -f bin/carina ]; then
  curl -sL https://download.getcarina.com/carina/latest/$(uname -s)/$(uname -m)/carina -o bin/carina
  chmod +x bin/carina
fi
carina=bin/carina

#### SETUP credentials

mkdir -p tmp
$carina credentials --path=tmp/build-creds $DEPLOY_CLUSTER > /dev/null
$carina credentials --path=certs $TARGET_CLUSTER > /dev/null

# learn about target 

source certs/docker.env
if [[ $DOCKER_TLS_VERIFY == 1 ]]; then
  target_docker_uri=$(echo $DOCKER_HOST | sed 's#tcp://#https://#')
else
  target_docker_uri=$(echo $DOCKER_HOST | sed 's#tcp://#http://#')
fi

#### BUILD
source tmp/build-creds/docker.env

docker build -t mccy .

vol_from=$(docker ps -q -a --filter 'label=mccy-data')

opts="-d --name $APP_NAME --restart=always"
if [[ $vol_from ]]; then
  opts="$opts --volumes-from $vol_from"
fi

running=$(docker ps -q -a --filter "name=$APP_NAME" --filter "status=running")
if [[ $running ]]; then
  docker stop $running
fi
exists=$(docker ps -q -a --filter "name=$APP_NAME")
if [[ $exists ]]; then
  docker rm $exists
fi

id=$(docker run $opts mccy \
  --security.user.password=$MCCY_PASSWORD \
  --mccy.docker-host-uri=$target_docker_uri \
  --mccy.docker-cert-path=/certs \
  --mccy.deployment-powered-by.image-src=images/powered-by/powered-by-carina-wide.png \
  --mccy.deployment-powered-by.href=https://getcarina.com/ \
  --spring.active-profiles=docker)

appPort=$(docker port $id 8080)
echo "
READY for use on $DEPLOY_CLUSTER at http://$appPort
"

if [[ -v CIRCLE_ARTIFACTS ]]; then
  cat <<EOF > $CIRCLE_ARTIFACTS/results.html
<html><body>
Ready for use on $DEPLOY_CLUSTER <a href="http://$appPort">here</a>
</body></html>
EOF

fi
