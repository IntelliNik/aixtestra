#!/bin/bash
# usage: deploy.sh
date=$(date +%s)
helm upgrade --install --namespace team-aixtra --values "deployment/values.gcloud.yaml" --set metadata.date="$date" solution deployment/chart