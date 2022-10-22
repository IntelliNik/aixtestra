#!/bin/bash
# usage: deploy.sh
docker build ./ocr -t europe-west3-docker.pkg.dev/creators-contest-2022/team-aixtra/ocr --platform linux/amd64
docker push europe-west3-docker.pkg.dev/creators-contest-2022/team-aixtra/ocr

date=$(date +%s)
helm upgrade --install --namespace team-aixtra --values "deployment/values.gcloud.yaml" --set metadata.date="$date" solution deployment/chart