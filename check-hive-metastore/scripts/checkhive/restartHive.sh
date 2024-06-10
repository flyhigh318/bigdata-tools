#!/bin/bash
# version: 1.0
# deps:
# Awk 4.0.2
# bash version 4.2.46(2)-release
# curl 7.29.0

set -eu -o pipefail
cd $(dirname $0)
readonly  ambariUrl="nn01.newc.sany.com:8080"
readonly  clusterName="newcProd"
readonly  user="admin:Rootnewc_123"
readonly  tmpfile="/tmp/.hiveApp.json"


function restartHostComponents() {
  local host="$1"
  cat > "${tmpfile}" << EOF
{
  "RequestInfo":{
      "command":"RESTART",
      "context":"Restart hiveserver components on nn01&nn02&hive host by Operation and maintenance R&D team bigdata devops",
      "operation_level":{"level":"HOST","cluster_name":"${clusterName}"}
    },
EOF
  if grep -qc "hive.newc.sany.com" <<< "${host}"
  then
    cat >> "${tmpfile}" << EOF
    "Requests/resource_filters":[
      {"service_name":"HIVE","component_name":"HIVE_METASTORE","hosts":"${host}"},
      {"service_name":"HIVE","component_name":"HIVE_SERVER","hosts":"${host}"}
    ]
}
EOF
  else
    cat >> "${tmpfile}" << EOF
    "Requests/resource_filters":[
      {"service_name":"HIVE","component_name":"HIVE_METASTORE","hosts":"${host}"}
    ]
}
EOF
  fi

  curl -m 600 -u "${user}" \
     -i -H 'X-Requested-By: Operation and maintenance R&D team' \
     --data-binary @"${tmpfile}" \
     http://"${ambariUrl}"/api/v1/clusters/"${clusterName}"/requests
  if test $? -ne 0;then
     echo "$(date +'%F %T') [ERROR] curl failed, please check."
  else
     echo "$(date +'%F %T') [INFO] restart hive  server on hosts ${host}"
  fi
}

hiveHost="$1"
restartHostComponents "${hiveHost}"