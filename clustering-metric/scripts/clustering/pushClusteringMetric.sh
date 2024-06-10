#!/bin/bash

cd $(dirname $0)
export PROME_SERVE_PUSH_GATEWAY="pushgateway.ngc.example.com.cn"
readonly today=$(date '+%Y%m%d')
readonly projectPath="/data/devops/tools"
readonly metricFile="${projectPath}/exampleClusteringMertic/logs/info_${today}.txt"

clusteringArray=(
  clustering.example_crane_ads.tb_hourly_data
  clustering.example_crane_ads.work_daily_count
  clustering.example_crane_ads.mysql_cluster_daily
  clustering.example_crane_ads.mysql_cluster_detail_daily
)

function pushHiveMetric() {
  # set gateway
  local promeServeGateway="$1"
  # hive metric
  local jobName="clustering"
  local instanceName="$2"
  local metricName="check_clustering_alive"
  local metricValue="$3"
  local labels="{categeory=\"hadoop\"}"
  local pushCmd="curl -s --data-binary @- http://${promeServeGateway}/metrics/job/${jobName}/instance/${instanceName}"
  cat << EOF | eval ${pushCmd}
${metricName} ${labels} ${metricValue}
EOF
}

for metric in  ${clusteringArray[@]}
do
   unset metricValue
   instance="${metric}"
   if grep -cq "${metric} SQL query successfully" "${metricFile}"
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