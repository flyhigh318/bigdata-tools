#!/bin/bash

projectPath="/opt/devops/tools"
logDir="${projectPath}/exampleClusteringMertic/logs"
today=$(date '+%Y%m%d')

[ ! -d "${logDir}" ] && mkdir -p "${logDir}"

nohup spark-submit \
  --class com.devops.clustering.App \
  --master yarn \
  --deploy-mode client \
  --name "example clustering metirc" \
  --driver-memory 1G \
  --driver-cores 1 \
  --num-executors 4 \
  --executor-memory 4g \
  --executor-cores 1 \
  clustering-example-metirc-1.0-SNAPSHOT.jar > "${logDir}"/info_${today}.txt 2> "${logDir}"/err_${today}.txt &