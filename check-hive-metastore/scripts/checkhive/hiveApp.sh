#!/bin/bash

cd $(dirname $0)
readonly today=$(date '+%Y%m%d')
readonly projectPath="/data/devops/tools"
readonly metricFile="${projectPath}/CheckHiveMetastore/logs/info_${today}.txt"

/bin/bash /data/devops/tools/runCheckMetastore.sh
/bin/bash /data/devops/tools/pushHiveMetric.sh >> /tmp/push_hive_metric.log 2>&1
hiveServerArray=(
  $(grep 'hive metastore connected failed' ${metricFile} \
     | awk -F'//' '{print $2}' \
     | awk -F":" '{print $1}')
)
if grep -qc "hive metastore connected failed" ${metricFile}
then
  for hiveHost in ${hiveServerArray[@]}
  do
    /bin/bash /data/devops/tools/restartHive.sh "${hiveHost}"
  done 
fi