CREATE CATALOG iceberg WITH (
    'type'='iceberg',
    'metastore' = 'hive',
    'uri'='thrift://hive-metastore:9083',
    'clients'='5',
    'property-version'='1',
    'warehouse'='s3a://datalake/iceberg'
);

CREATE DATABASE IF NOT EXISTS iceberg.flink;
set sql-client.execution.runtime-mode=BATCH;
set sql-client.execution.result-mode=TABLEAU;
set sql-client.verbose=true;
