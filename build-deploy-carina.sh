#!/bin/bash

usage() {
  echo "Usage: $0 DEPLOY_CLUSTER PINNED_NODE"
}

check_var() {
  if [[ ! -v $1 || -z $1 ]]; then
    echo "Missing environment variable $1"
    exit 1
  fi
}

check_volume() {
  if docker volume &> /dev/null; then
    if docker volume ls |awk "\$2 == \"$1\"{++m} END {exit (m>0?0:1)}"; then
      return
    else
      echo "Missing required volume $1"
      exit 1
    fi
  else
    echo "WARN: 'docker volume' not supported so we'll have to trust $1 exists"
  fi
}

if [[ $* < 2 ]]; then
  usage
  exit 1
fi
DEPLOY_CLUSTER=$1
export PINNED_NODE=$2

check_var TARGET_CLUSTER
check_var CARINA_USERNAME
check_var CARINA_APIKEY
check_var MCCY_PASSWORD
check_var CIRCLE_BRANCH
check_var LETSENCRYPT_EMAIL
check_var LETSENCRYPT_DOMAIN

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
  export TARGET_DOCKER_URI=$(echo $DOCKER_HOST | sed 's#tcp://#https://#')
else
  export TARGET_DOCKER_URI=$(echo $DOCKER_HOST | sed 's#tcp://#http://#')
fi

#### BUILD AND DEPLOY
source tmp/build-creds/docker.env

check_volume dhparam-cache
check_volume letsencrypt
check_volume letsencrypt-backups
check_volume mccy

# Ensure the latest of our app image is always built
docker-compose -p $CIRCLE_BRANCH build --pull
# ...and ensure proxy image is the latest
docker-compose -p $CIRCLE_BRANCH pull

docker-compose --verbose -p $CIRCLE_BRANCH up -d

echo "
READY for use on $DEPLOY_CLUSTER
"

if [[ -v CIRCLE_ARTIFACTS ]]; then
  cat <<EOF > $CIRCLE_ARTIFACTS/results.html
<html><body>
Ready for use on $DEPLOY_CLUSTER
</body></html>
EOF

fi
