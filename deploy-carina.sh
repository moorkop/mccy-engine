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

#### SETUP credentials

mkdir carina
docker pull itzg/carina-cli
docker run --rm -v $(pwd)/carina:/carina -e CARINA_USERNAME -e CARINA_APIKEY itzg/carina-cli credentials $DEPLOY_CLUSTER
source carina/clusters/$CARINA_USERNAME/$DEPLOY_CLUSTER/docker.env

#### DEPLOY

check_volume dhparam-cache
check_volume letsencrypt
check_volume letsencrypt-backups

export COMPOSE_PROJECT_NAME="${CIRCLE_BRANCH}_mccy"
docker-compose -f docker-compose-carina.yml pull
docker-compose -f docker-compose-carina.yml up -d

echo "
READY for use on the cluster $DEPLOY_CLUSTER
"

if [[ -v CIRCLE_ARTIFACTS ]]; then
  cat <<EOF > $CIRCLE_ARTIFACTS/results.html
<html><body>
Ready for use on the cluster $DEPLOY_CLUSTER
</body></html>
EOF

fi
