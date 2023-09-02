#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE metastore;
    CREATE USER hive WITH ENCRYPTED PASSWORD 'hive';
    GRANT ALL PRIVILEGES ON DATABASE metastore TO hive;

    CREATE DATABASE airflow;
    CREATE USER airflow WITH ENCRYPTED PASSWORD 'airflow';
    GRANT ALL PRIVILEGES ON DATABASE metastore TO airflow;
EOSQL
