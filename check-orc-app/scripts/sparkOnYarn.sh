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