TAG = "MAKE"

help:
	@echo "The following make targets are available:"
	@echo ""
	@echo "	 trino.cli\t		Execute trino-cli"
	@echo "	 trino.shell\t\t 	Access trino-coordinator"
	@echo ""
	@echo "	 compose.trino\t\t 	Run trino-related containers"
	@echo ""
	@echo "	 precommit\t		Execute pre-commit hooks"
	@echo "	 changelog\t	 	Update 'CHANGELOG.md'"
	@echo ""

.PHONY: trino.cli
trino.cli:
	trino --server http://localhost:8889

.PHONY: trino.shell
trino.shell:
	docker exec -w /etc/trino -it trino /bin/bash

.PHONY: compose.trino
compose.trino:
	COMPOSE_PROFILES=trino docker-compose up


.PHONY: precommit
precommit:
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
