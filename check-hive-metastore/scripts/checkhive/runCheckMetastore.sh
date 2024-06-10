#!/bin/bash
export PATH=/usr/lib64/qt-3.3/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/usr/local/java/jdk1.8.0_311/bin:/root/bin

cd $(dirname $0)
projectPath="/data/devops/tools"
logDir="${projectPath}/CheckHiveMetastore/logs"
today=$(date '+%Y%m%d')
now=$(date '+%Y%m%d%H%M')
hiveMetaStoreUrls=(
  thrift://hive.newc.sany.com:9083
  thrift://nn01.newc.sany.com:9083
  thrift://nn02.newc.sany.com:9083
)

[ ! -d "${logDir}" ] && mkdir -p "${logDir}"

if test -f "${logDir}"/info_${today}.txt
then
 rm -rf  "${logDir}"/info_${today}.txt  "${logDir}"/err_${today}.txt
 touch "${logDir}"/info_${today}.txt
 touch "${logDir}"/err_${today}.txt
fi

for hiveMetastoreUrl in ${hiveMetaStoreUrls[@]}
do
  timeout 120s spark-submit \
  --class com.devops.check.metastore.App \
  --master local[1] \
  --deploy-mode client \
  --driver-memory 512m \
  --driver-cores 1 \
  ${projectPath}/bigdata-tools-1.0-SNAPSHOT.jar \
  --metastore-url ${hiveMetastoreUrl} >> "${logDir}"/info_${today}.txt 2>> "${logDir}"/err_${today}.txt || {
     echo "$(date +'%T %F') [ERROR] timeout 120 seconds ${hiveMetastoreUrl}" >> "${logDir}"/timeout_kill_${now}.txt
     hiveHost=$( echo ${hiveMetastoreUrl} \
       | awk -F'//' '{print $2}' \
       | awk -F":" '{print $1}')
     /bin/bash /data/devops/tools/restartHive.sh "${hiveHost}"
  }
done