CREATE TABLE raw_products (
                              origin_ts TIMESTAMP(3) METADATA FROM 'value.ingestion-timestamp' VIRTUAL,
                              event_time TIMESTAMP(3) METADATA FROM 'value.source.timestamp' VIRTUAL,
                              origin_database STRING METADATA FROM 'value.source.database' VIRTUAL,
                              origin_schema STRING METADATA FROM 'value.source.schema' VIRTUAL,
                              origin_table STRING METADATA FROM 'value.source.table' VIRTUAL,
                              origin_properties MAP<STRING, STRING> METADATA FROM 'value.source.properties' VIRTUAL,
                              id BIGINT,
                              name STRING,
                              description STRING,
                              weight DECIMAL(38, 10)

) WITH (
    'connector' = 'kafka',
    'topic' = 'cdc.inventory.data.inventory.products',
    'properties.bootstrap.servers' = 'kafka1:29092',
    'properties.group.id' = 'testGroup',
    'properties.auto.offset.reset' = 'earliest',
    'scan.startup.mode' = 'earliest-offset',
    'format' = 'debezium-json',
    'debezium-json.schema-include' = 'true',
    'debezium-json.ignore-parse-errors' = 'false'
);

CREATE TABLE iceberg.flink.inventory_products (
                                                  id BIGINT,
                                                  name STRING,
                                                  description STRING,
                                                  weight DECIMAL(38, 10),
                                                  PRIMARY KEY (`id`) NOT ENFORCED
) WITH (
    'catalog-name'='iceberg',
    'catalog-database'='flink',
    'catalog-type'='hive',
    'uri'='thrift://hive-metastore:9083',
    'warehouse'='s3a://datalake/iceberg',
    'format-version'='2',
    'write.parquet.compression-codec'='zstd',
    'write.parquet.compression-level'='9',
    'write.distribution-mode'='none',
    'write.update.mode'='merge-on-read',
    'write.delete.mode'='merge-on-read',
    'write.merge.mode'='merge-on-read',
    'write.upsert.enabled'='true'
);

SET 'execution.checkpointing.interval' = '30s';
SET 'parallelism.default' = '2';
SET 'pipeline.max-parallelism' = '12';
SET 'execution.runtime-mode' = 'streaming';
SET pipeline.name='INSERT_inventory_products';
INSERT INTO iceberg.flink.inventory_products SELECT id, name, description, weight FROM raw_products;

RESET pipeline.name;
SET 'execution.runtime-mode' = 'batch';
SET 'sql-client.execution.result-mode' = 'tableau';
SELECT * FROM iceberg.flink.inventory_products;
