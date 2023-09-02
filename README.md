# Lakehouse Playground

- [x] Spark 3.3 ~ 3.4 (Iceberg 1.3.1, Hudi 0.13.1)
- [x] Flink 1.16 ~ 1.17 (Iceberg 1.3.1, Hudi 0.13.1)
- [x] Trino 425
- [x] Airflow 2.7.0 
- [x] Jupyterlab

## Getting Started

Execute compose containers first.

```bash
# Use `COMPOSE_PROFILES` to select the profile
COMPOSE_PROFILES=trino docker-compose up;
COMPOSE_PROFILES=spark docker-compose up;
COMPOSE_PROFILES=flink docker-compose up;
COMPOSE_PROFILES=airflow docker-compose up;

# Combine multiple profiles
COMPOSE_PROFILES=trino,spark docker-compose up;
```

Then access the lakehouse services. 

- Trino: http://localhost:8889
- Airflow (`airflow` / `airflow`) : http://localhost:8080
- Local S3 Minio (`minio` / `minio123`): http://localhost:9000
- PySpark Jupyter Notebook (Iceberg): http://localhost:8900
- PySpark Jupyter Notebook (Hudi): http://localhost:8901
- Spark SQL (Iceberg): `docker exec -it spark-iceberg spark-sql`
- Spark SQL (Hudi): `docker exec -it spark-hudi spark-sql`
- Flink SQL (Iceberg): `docker exec -it flink-jobmanager flink-sql-iceberg`
- Flink SQL (Hudi): `docker exec -it flink-jobmanager flink-sql-hudi;`