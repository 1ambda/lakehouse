CREATE CATALOG hudi WITH (
    'type'='hudi',
    'mode'='hms',
    'hive.conf.dir'='/opt/flink/conf',
    'catalog.path'='s3a://datalake/hudi',
    'table.external'='true'
);

CREATE DATABASE IF NOT EXISTS hudi.flink;

set sql-client.execution.runtime-mode=BATCH;
set sql-client.execution.result-mode=TABLEAU;
set sql-client.verbose=true;
