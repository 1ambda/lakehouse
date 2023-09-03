#!/bin/bash

export HADOOP_CLASSPATH=`$HADOOP_HOME/bin/hadoop classpath`

${FLINK_HOME}/bin/sql-client.sh embedded --init $SQL_CLIENT_HOME/flink-init-hudi.sql \
  -D sql-client.verbose=true \
  -D sql-client.execution.result-mode=TABLEAU \
   shell
