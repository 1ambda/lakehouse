package com.github.lambda.lakehouse

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}


fun main() {
    val tableEnv: TableEnvironment = buildTableEnvironment()

    val tableRawCustomers: Table = buildSourceTable("raw_customers", tableEnv)
    val tableAggrCustomers: Table = buildSinkTable("aggr_customers", tableEnv)

    logger.info { "Table Raw Customers: $tableRawCustomers" }

    tableEnv.executeSql("INSERT INTO aggr_customers SELECT state, trace, worker_id FROM raw_customers")

}

fun buildTableEnvironment(): StreamTableEnvironment {
    // TODO (Kun): Handle Parameters
    // - https://nightlies.apache.org/flink/flink-docs-master/docs/dev/datastream/application_parameters/
    val conf = Configuration()
    val env = StreamExecutionEnvironment
            .createLocalEnvironmentWithWebUI(conf)
    env.checkpointConfig.checkpointInterval = 10000L
    //    env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
    //    env.setStateBackend(new EmbeddedRocksDBStateBackend());
    //    env.getCheckpointConfig().setCheckpointStorage("file:///tmp/flink-checkpoint");
    //    env.setDefaultSavepointDirectory("file:///tmp/flink-savepoint");
    return StreamTableEnvironment.create(env)
}


fun buildSinkTable(tableName: String, tableEnv: TableEnvironment): Table {
    val query = """
        CREATE TABLE $tableName (
           state STRING,
           trace STRING,
           worker_id STRING
        ) PARTITIONED BY (worker_id) WITH (
         'connector' = 'filesystem',
         'format' = 'parquet',
         'path' = 's3://datalake/parquet/test'
        );
    """
    tableEnv.executeSql(query)
    tableEnv.executeSql("SHOW CREATE TABLE $tableName").print()
    return tableEnv.from(tableName)
}

fun buildSourceTable(tableName: String, tableEnv: TableEnvironment): Table {
    val query = """
        CREATE TABLE $tableName (
           state STRING,
           trace STRING,
           worker_id STRING
        ) WITH (
           'connector' = 'kafka',
           'topic' = 'connect-cluster.inventory.status',
           'properties.bootstrap.servers' = 'localhost:9092',
           'properties.group.id' = 'testGroup',
           'properties.auto.offset.reset' = 'earliest',
           'scan.startup.mode' = 'earliest-offset',
           'format' = 'json',   'json.ignore-parse-errors' = 'true',
           'json.fail-on-missing-field' = 'false'
        );
    """
    tableEnv.executeSql(query)
    tableEnv.executeSql("SHOW CREATE TABLE $tableName").print()
    return tableEnv.from(tableName)
}
