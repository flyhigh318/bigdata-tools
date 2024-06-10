#!/bin/bash

cd $(dirname $0)
export PROME_SERVE_PUSH_GATEWAY="pushgateway.ngc.sany.com.cn"
readonly today=$(date '+%Y%m%d')
readonly projectPath="/data/devops/tools"
readonly metricFile="${projectPath}/CheckHiveMetastore/logs/info_${today}.txt"

hiveMetaStoreUrls=(
  thrift://hive.newc.sany.com:9083
  thrift://nn01.newc.sany.com:9083
  thrift://nn02.newc.sany.com:9083
)

function pushHiveMetric() {
  # set gateway
  local promeServeGateway="$1"
  # hive metric
  local jobName="hive"
  local instanceName="$2"
  local metricName="check_hive_metastore_alive"
  local metricValue="$3"
  local labels="{categeory=\"hadoop\"}"
  local pushCmd="curl -s --data-binary @- http://${promeServeGateway}/metrics/job/${jobName}/instance/${instanceName}"
  cat << EOF | eval ${pushCmd}
${metricName} ${labels} ${metricValue}
EOF
}

for metric in  ${hiveMetaStoreUrls[@]}
do
   unset metricValue
   instance=$(echo "${metric}" | awk -F'//' '{print $2}' | awk -F":" '{print $1}')
   if grep -cq "check hive metastore connected successfully: ${metric}" "${metricFile}"
   then
     metricValue=1
   else
     metricValue=0
   fi
   pushHiveMetric ${PROME_SERVE_PUSH_GATEWAY} ${instance} ${metricValue}
   if [ $? -eq 0 ];then
     echo "$(date +'%F %T') [INFO] push  metric ${instance} ${metricValue} succeesfully."
   else
     echo "$(date +'%F %T') [ERROR] push  metric ${instance} ${metricValue} failed."
   fi
done 