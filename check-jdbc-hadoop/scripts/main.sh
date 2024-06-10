#!/bin/bash

cat > /tmp/application.proerties << 'EOF'
app.jdbc.url=jdbc:hive2://localhost:10000/default
app.version=1.0
app.sql=show databases
app.driver.name=org.apache.hive.jdbc.HiveDriver
EOF

java -cp /usr/hdp/3.0.0.0-1634/spark2/jars/*:check-jdbc-hadoop.jar com.devops.check.hadoop.jdbc.App \
  --file application.proerties