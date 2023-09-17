package com.github.lambda.lakehouse;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlinkAppKafkaToParquet {

  private static final Logger LOG = LoggerFactory.getLogger(FlinkAppKafkaToParquet.class);

  public static void main(String[] args) throws Exception {

    TableEnvironment tableEnv = buildTableEnvironment();

    Table tableRawCustomers = buildSourceTable("raw_customers", tableEnv);
    Table tableAggrCustomers = buildSinkTable("aggr_customers", tableEnv);

    tableEnv.executeSql("INSERT INTO aggr_customers SELECT state, trace, worker_id FROM raw_customers");
  }

  public static Table buildSinkTable(String tableName, TableEnvironment tableEnv) {
    String query = ""
        + "CREATE TABLE " + tableName + " (\n"
        + "   state STRING,\n"
        + "   trace STRING,\n"
        + "   worker_id STRING\n"
        + ") PARTITIONED BY (worker_id) "
        + "WITH (\n"
        + " 'connector' = 'filesystem',\n"
        + " 'format' = 'parquet',\n"
        + " 'path' = 's3://datalake/parquet/test'\n"
        + ");\n";
    tableEnv.executeSql(query);
    tableEnv.executeSql("SHOW CREATE TABLE " + tableName).print();

    Table table = tableEnv.from(tableName);

    return table;
  }

  public static Table buildSourceTable(String tableName, TableEnvironment tableEnv) {
    String query = ""
        + "CREATE TABLE " + tableName + " (\n"
        + "   state STRING,\n"
        + "   trace STRING,\n"
        + "   worker_id STRING\n" + ") "
        + "WITH (\n"
        + "   'connector' = 'kafka',\n"
        + "   'topic' = 'connect-cluster.inventory.status',\n"
        + "   'properties.bootstrap.servers' = 'localhost:9092',\n"
        + "   'properties.group.id' = 'testGroup',\n"
        + "   'properties.auto.offset.reset' = 'earliest',\n"
        + "   'scan.startup.mode' = 'earliest-offset',\n"
        + "   'format' = 'json',"
        + "   'json.ignore-parse-errors' = 'true',\n"
        + "   'json.fail-on-missing-field' = 'false'\n"
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
