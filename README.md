# Lakehouse Playground

- [x] Spark 3.4 + Iceberg 1.3.1
- [x] Spark 3.3 + Hudi 0.13.1
- [x] Flink 1.16 + Iceberg 1.3.1 
- [x] Flink 1.16 + Hudi 0.13.1
- [x] Trino 425


## Getting Started

```bash
# Spark SQL with Iceberg 
docker exec -it spark-iceberg spark-sql

# Spark SQL with Hudi 
docker exec -it spark-hudi spark-sql

# Trino Client with Iceberg, Hudi 
trino --server http://localhost:8889

# Flink SQL with Iceberg 
docker exec -it flink-jobmanager flink-sql-iceberg;

# Flink SQL with Hudi 
docker exec -it flink-jobmanager flink-sql-hudi;

```
