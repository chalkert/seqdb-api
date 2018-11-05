# seqdb-api [![Build Status](https://travis-ci.org/AAFC-BICoE/seqdb-api.svg?branch=dev)](https://travis-ci.org/AAFC-BICoE/seqdb-api)

Sequence management services managing laboratory workflows leading to DNA sequences.

seqdb-api is an implementation of the Sequence Module for the [DINA project](https://www.dina-project.net/).

## Status
Currently under development, see [Release Notes](RELEASE_NOTES.md) for details.

## Required

* Java 1.8+
* Maven 3.2+

## To launch

```
mvn clean spring-boot:run
```

## To run test

Unit tests:
```
mvn clean test
```

All tests (Integration + Unit):
```
mvn clean verify
```

## To package

```
mvn clean install
```

This will create an executable jar.

## To run the packaged application (using an embedded Tomcat instance)

```
java -jar seqdb.api.jar
```

You can also include configuration from an external file:

```
java -jar seqdb.api.jar --spring.config.additional-location=./myconfig.yml
```

## Generate Maven reports

* Checkstyle
* SpotBugs
* OWASP dependency-check
* Jacoco

```
mvn clean verify site
```

## Configuration

This application is configured using Spring Boot, with default properties stored in src/main/resources/application.yml. It can be [configured externally using properties files, YAML files, environment variables, and command-line arguments](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

* [Common Spring properties](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)
* [Crnk properties](http://www.crnk.io/releases/stable/documentation/#_integration_with_spring_and_string_boot)

* Importing Sample Accounts: When running the application locally with an in-memory or MySQL database it may be useful to have 2 existing Accounts that you can log into. To import the Admin (username=Admin, password=Admin) and User (username=User, password=User) accounts, set the configuration property "import-sample-accounts" to true. **Warning:** This is a dangerous argument to run on production because it will import an admin account with an easy password. You should only pass this argument from the command line or from an external configuration file that is only used on a local dev instance, instead of src/resources/application.yml.

* Automatically trimming string input: Setting "seqdb.trim-resource-string-properties" to true will remove whitespace from the beginning and end of all string properties on created or updated resources.

## Database migration with Liquibase

This application uses Liquibase to generate the database and migrate to new versions of the database. Changelogs are source xml files that describe schema changes to the database. Liquibase maintains a "DATABASECHANGELOG" table in the same database to keep track of which changelogs have been executed. When the application starts, any changelogs that have not been executed are executed.

Our changelogs are kept in src/main/resources/db/changelog.

When adding a new migration changelog, put the new file in src/main/resources/db/changelog/migrations, and name the file after the issue, e.g. db.changelog-Feature\_15\_Add\_Sample\_Table.xml". Then add the new file's path to src/main/resources/db/changelog/db.changelog-master.xml in an "include" tag.

 * [Liquibase home](http://www.liquibase.org/index.html)
 * [Liquibase Best Practices](http://www.liquibase.org/bestpractices.html)
 * [Change example: addColumn](https://www.liquibase.org/documentation/changes/add_column.html)
 * [Adding Liquibase support to an existing database](https://www.liquibase.org/documentation/generating_changelogs.html)

## Examples

Create a new Region:
```
curl -XPOST -H "Content-Type: application/vnd.api+json" \
--data '{"data":{"type": "region", "attributes": {"name":"My Region", "description":"My Description", "symbol":"My Symbol"}}}' \
http://localhost:8080/api/region
```
