TAG = "MAKE"

help:
	@echo "The following make targets are available:"
	@echo "	 changelog 		Update CHANGELOG.md"

.PHONY: changelog
changelog:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Updating CHANGELOG.md"
	@ $(git-changelog -bio CHANGELOG.md -c angular)
	@ echo ""
	@ echo ""
