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

.PHONY: compose.trino
compose.trino:
	COMPOSE_PROFILES=trino docker-compose up

.PHONY: compose.dbt
compose.dbt:
	COMPOSE_PROFILES=trino,airflow docker-compose up

.PHONY:test
test:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Executing pytest"
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
