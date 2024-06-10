# bigdata-tools 
The engineering project is divided into four sub-projects: check-orc-app, check-hive-metastore, check-jdbc-hadoop, and clustering-metric. 
The specific scripts are under the scripts directory in each sub-project.


## usage of check-orc-app
```
#!/bin/bash
# deploy spark on yarn

cat > /tmp/application.proerties <<'EOF'
app.hdfs.namespace.url=hdfs://saasProduction
app.hdfs.path.orc=/warehouse/modeling/ods/data/5f867ef96e5dad004d557644/buflt_5006_model_10135/471823,/warehouse/modeling/ods/data/5f867ef96e5dad004d557644/buflt_5006_model_10135/471825
app.version=1.0
app.thread.mumber=32
app.spark.coalesce.number=5
app.move.corrupt.orc=false
EOF

nohup spark-submit \
  --class com.devops.App \
  --master yarn \
  --deploy-mode client \
  --name "check orc file" \
  --driver-memory 50G \
  --driver-cores 16 \
  --num-executors 4 \
  --executor-memory 4g \
  --executor-cores 1 \
  --conf spark.sql.hive.filesourcePartitionFileCacheSize=4097152000 \
  check-orc-app-1.0-SNAPSHOT.jar --file application.proerties > info.txt 2> err.txt &
```

## usage of check-hive-metastore
```
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
```

## usage of check-jdbc-hadoop
```
#!/bin/bash

cat > /tmp/application.proerties << 'EOF'
app.jdbc.url=jdbc:hive2://localhost:10000/default
app.version=1.0
app.sql=show databases
app.driver.name=org.apache.hive.jdbc.HiveDriver
EOF

java -cp /usr/hdp/3.0.0.0-1634/spark2/jars/*:check-jdbc-hadoop.jar com.devops.check.hadoop.jdbc.App \
  --file application.proerties
```

## usage of clustering-metric
```
#!/bin/bash

projectPath="/opt/devops/tools"
logDir="${projectPath}/SanyClusteringMertic/logs"
today=$(date '+%Y%m%d')

[ ! -d "${logDir}" ] && mkdir -p "${logDir}"

nohup spark-submit \
  --class com.devops.clustering.App \
  --master yarn \
  --deploy-mode client \
  --name "sany clustering metirc" \
  --driver-memory 1G \
  --driver-cores 1 \
  --num-executors 4 \
  --executor-memory 4g \
  --executor-cores 1 \
  clustering-sany-metirc-1.0-SNAPSHOT.jar > "${logDir}"/info_${today}.txt 2> "${logDir}"/err_${today}.txt &
```



