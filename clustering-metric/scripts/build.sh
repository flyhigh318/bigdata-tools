#!/bin/bash
cd $(dirname $0)
cd ..
mvn clean && mvn package