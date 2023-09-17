TAG = "MAKE"

help:
	@echo "The following make targets are available:"
	@echo ""
	@echo "	 trino.cli\t		Execute trino-cli"
	@echo "	 trino.shell\t\t 	Access trino-coordinator"
	@echo "	 airflow.shell\t\t 	Access airflow-scheduler"
	@echo ""
	@echo "	 compose.trino\t\t 	Run trino-related containers"
	@echo "	 compose.dbt\t\t 	Run dbt-related containers"
	@echo ""
	@echo "	 check\t			Execute pre-commit hooks"
	@echo "	 changelog\t	 	Update 'CHANGELOG.md'"
	@echo ""

.PHONY: trino.cli
trino.cli:
	trino --server http://localhost:8889

.PHONY: trino.shell
trino.shell:
	docker exec -w /etc/trino -it trino /bin/bash

.PHONY: airflow.shell
airflow.shell:
	docker exec -it airflow-scheduler /bin/bash

.PHONY: mysql.shell
mysql.shell:
	mycli -u root -p admin                                                                                                                                                                                                                  │

.PHONY: debezium.register
debezium.register: debezium.register.customers debezium.register.products

.PHONY: debezium.register.customers
debezium.register.customers:
	curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ -d @docker/debezium/register.inventory_customers.json

.PHONY: debezium.register.products
debezium.register.products:
	curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ -d @docker/debezium/register.inventory_products.json

.PHONY: compose.trino
compose.trino:
	COMPOSE_PROFILES=trino docker-compose up

.PHONY: compose.dbt
compose.dbt:
	COMPOSE_PROFILES=trino,airflow docker-compose up

.PHONY: compose.cdc
compose.cdc:
	COMPOSE_PROFILES=kafka docker-compose -f docker-compose-cdc.yml up

.PHONY: compose.stream
compose.stream:
	 COMPOSE_PROFILES=kafka docker-compose -f docker-compose.yml -f docker-compose-cdc.yml up

.PHONY: compose.clean
compose.clean:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Cleaning container volumes ('docker/volume')"
	@ rm -rf docker/volume
	@ docker container prune -f
	@ docker volume prune -f
	@ echo ""
	@ echo ""

.PHONY:prepare
prepare:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Prepare local environment"
	@ brew install pyenv pyenv-virtualenv
	@ pip3 install poetry
	@ pip3 install --upgrade pip
	@ poetry install
	@ pre-commit install
	@ pre-commit run
	@ echo ""
	@ echo ""

.PHONY:test
test:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Executing tests"
	@ AIRFLOW_HOME=$(shell pwd) poetry run pytest dags-test/
	@ echo ""
	@ echo ""

.PHONY:check
check:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Executing pre-commit hooks"
	@ pre-commit run
	@ echo ""
	@ echo ""

.PHONY: changelog
changelog:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Updating CHANGELOG.md"
	@ $(git-changelog -bio CHANGELOG.md -c angular)
	@ echo ""
	@ echo ""
