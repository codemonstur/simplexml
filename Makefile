.PHONY: help clean build test validate release-notes deploy

NAME=`xmllint --xpath "//project/artifactId/text()" pom.xml`
VERSION=`xmllint --xpath "//project/version/text()" pom.xml`
PREVIOUS_TAG=`git tag | sort -r | head -n 1`

# Blue [INFO] prefix before every message, NO_COLOR=1 prints it plain
ifneq ($(NO_COLOR),)
INFO = printf '[INFO] %s\n'
else
INFO = printf '[\033[1;34mINFO\033[m] %s\n'
endif

help:
	@echo "Available targets for $(NAME):"
	@echo "\thelp\t\t\tThis help"
	@echo "\tclean\t\t\tDelete everything in ./target"
	@echo "\tbuild\t\t\tCleans the project and rebuilds the code"
	@echo "\ttest\t\t\tRuns the tests"
	@echo "\tvalidate\t\tCheck dependency versions, cves, and licenses"
	@echo "\trelease-notes\t\tCreate release notes for the latest version"
	@echo "\tdeploy\t\t\tClean, build and deploy a version to Github and Maven Central"

clean:
	@$(INFO) "Cleaning"
	@mvn clean

build:
	@$(INFO) "Building"
	@mvn -DskipTests clean package

test:
	@mvn test

validate:
	@mvn versions:display-dependency-updates
	@mvn versions:display-plugin-updates
	@mvn validate -P vulnerabilities
	@mvn validate -P licenses

release-notes:
	@$(INFO) "Writing release notes to src/docs/releases/release-$(VERSION).txt"
	@echo "$(VERSION)" >> src/docs/releases/release-$(VERSION).txt
	@echo "" >> src/docs/releases/release-$(VERSION).txt
	@git log --pretty="%s" $(PREVIOUS_TAG)... master >> src/docs/releases/release-$(VERSION).txt

deploy: build
	@$(INFO) "Uploading to maven central"
	@mvn clean deploy
	@$(INFO) "Creating github release"
	@gh release create $(NAME)-$(VERSION) target/$(NAME)-$(VERSION).jar target/$(NAME)-$(VERSION)-javadoc.jar target/$(NAME)-$(VERSION)-sources.jar -F src/docs/releases/release-$(VERSION).txt
	@$(INFO) "Tagging and pushing to github"
	@git tag $(NAME)-$(VERSION)
	@git push && git push --tags
