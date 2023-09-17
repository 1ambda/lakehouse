package com.github.lambda.lakehouse;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlinkAppKafkaToIceberg {

  private static final Logger LOG = LoggerFactory.getLogger(FlinkAppKafkaToIceberg.class);

  public static void main(String[] args) throws Exception {

    TableEnvironment tableEnv = buildTableEnvironment();

    Table tableRawCustomers = buildSourceTable("raw_customers", tableEnv);
    Table tableAggrCustomers = buildSinkTable("inventory", "sink_customers", tableEnv);

    tableEnv.executeSql("INSERT INTO sink_customers SELECT id, weight FROM raw_customers");
  }

  public static Table buildSinkTable(String dbName, String tableName, TableEnvironment tableEnv) {
    String query = ""
        + "CREATE TABLE " + dbName + "." + tableName + " (\n"
        + "   id BIGINT,\n"
        + "   weight DECIMAL(38, 10),\n"
        + "   PRIMARY KEY (id) NOT ENFORCED\n"
        + ") "
        + "WITH (\n"
        + " 'connector' = 'iceberg',\n"
        + " 'catalog-name' = 'hive_prod',\n"
        + " 'catalog-database' = '" + dbName + "',\n"
        + " 'catalog-table' = 'customers',\n"
        + " 'uri' = 'thrift://localhost:9083',\n"
        + " 'warehouse' = 's3a://datalake'\n"
        + ");\n";
    tableEnv.executeSql(query);
    tableEnv.executeSql("SHOW CREATE TABLE " + dbName + "." + tableName).print();

    Table table = tableEnv.from(dbName + "." + tableName);

    return table;
  }

  public static Table buildSourceTable(String tableName, TableEnvironment tableEnv) {
    String query = ""
        + "CREATE TABLE " + tableName + " (\n"
        + "   origin_ts TIMESTAMP(3) METADATA FROM 'value.ingestion-timestamp' VIRTUAL,\n"
        + "   event_time TIMESTAMP(3) METADATA FROM 'value.source.timestamp' VIRTUAL,\n"
        + "   origin_database STRING METADATA FROM 'value.source.database' VIRTUAL,\n"
        + "   origin_schema STRING METADATA FROM 'value.source.schema' VIRTUAL,\n"
        + "   origin_table STRING METADATA FROM 'value.source.table' VIRTUAL,\n"
        + "   origin_properties MAP<STRING, STRING> METADATA FROM 'value.source.properties' VIRTUAL,\n"
        + "   id BIGINT,\n"
        + "   name STRING,\n"
        + "   description STRING,\n"
        + "   weight DECIMAL(38, 10)\n" + ") "
        + "WITH (\n"
        + " 'connector' = 'kafka',\n"
        + " 'topic' = 'cdc-json.inventory.data.inventory.customers',\n"
        + " 'properties.bootstrap.servers' = 'localhost:9092',\n"
        + " 'properties.group.id' = 'testGroup',\n"
        + " 'properties.auto.offset.reset' = 'earliest',\n"
        + " 'scan.startup.mode' = 'earliest-offset',\n"
        + " 'format' = 'debezium-json',\n"
        + " 'debezium-json.schema-include' = 'true',\n"
        + " 'debezium-json.ignore-parse-errors' = 'false'\n"
        + ");\n";
    tableEnv.executeSql(query);
    tableEnv.executeSql("SHOW CREATE TABLE " + tableName).print();

    Table table = tableEnv.from(tableName);

    return table;
  }

  public static StreamTableEnvironment buildTableEnvironment() {
    // TODO (Kun): Handle Parameters
    // - https://nightlies.apache.org/flink/flink-docs-master/docs/dev/datastream/application_parameters/
    Configuration conf = new Configuration();
    StreamExecutionEnvironment env = StreamExecutionEnvironment
        .createLocalEnvironmentWithWebUI(conf);
    env.getCheckpointConfig().setCheckpointInterval(10000L);
//    env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//    env.setStateBackend(new EmbeddedRocksDBStateBackend());
//    env.getCheckpointConfig().setCheckpointStorage("file:///tmp/flink-checkpoint");
//    env.setDefaultSavepointDirectory("file:///tmp/flink-savepoint");

    StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

    return tableEnv;
  }
}
