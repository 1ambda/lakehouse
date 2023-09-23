package com.github.lambda.lakehouse

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

private val logger = KotlinLogging.logger {}


fun main() {
    val tableEnv: TableEnvironment = buildTableEnvironment()

    val tableRawCustomers: Table = buildSourceTable("raw_customers", tableEnv)
    val tableAggrCustomers: Table = buildSinkTable("inventory", "aggr_customers", tableEnv)

    logger.info { "Table Raw Customers: $tableRawCustomers" }
    logger.info { "Table Raw Customers: $tableAggrCustomers" }

    tableEnv.executeSql("INSERT INTO aggr_customers SELECT id, weight FROM raw_customers")

}

fun buildTableEnvironment(): StreamTableEnvironment {
    // TODO (Kun): Handle Parameters
    // - https://nightlies.apache.org/flink/flink-docs-master/docs/dev/datastream/application_parameters/
    val conf = Configuration()
    val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)
    env.checkpointConfig.checkpointInterval = 10000L
    //    env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
    //    env.setStateBackend(new EmbeddedRocksDBStateBackend());
    //    env.getCheckpointConfig().setCheckpointStorage("file:///tmp/flink-checkpoint");
    //    env.setDefaultSavepointDirectory("file:///tmp/flink-savepoint");
    return StreamTableEnvironment.create(env)
}

fun buildSinkTable(dbName: String, tableName: String, tableEnv: TableEnvironment): Table {
    val query = """
    CREATE TABLE $tableName (
       id BIGINT,
       weight DECIMAL(38, 10),
       PRIMARY KEY (id) NOT ENFORCED
    ) WITH (
        'connector' = 'iceberg',
        'catalog-name' = 'iceberg',
        'catalog-database' = '$dbName',
        'catalog-table' = '$tableName',
        'uri' = 'thrift://localhost:9083',
        'warehouse' = 's3a://datalake'
    );
    """
    tableEnv.executeSql(query)
    tableEnv.executeSql("SHOW CREATE TABLE $tableName").print()
    return tableEnv.from("$tableName")
}

fun buildSourceTable(tableName: String, tableEnv: TableEnvironment): Table {
    val query = """
    CREATE TABLE $tableName (
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
        'topic' = 'cdc-json.inventory.data.inventory.customers',
        'properties.bootstrap.servers' = 'localhost:9092',
        'properties.group.id' = 'testGroup',
        'properties.auto.offset.reset' = 'earliest',
        'scan.startup.mode' = 'earliest-offset',
        'format' = 'debezium-json',
        'debezium-json.schema-include' = 'true',
        'debezium-json.ignore-parse-errors' = 'false'
    );
    """
    tableEnv.executeSql(query)
    tableEnv.executeSql("SHOW CREATE TABLE $tableName").print()
    return tableEnv.from(tableName)
}
