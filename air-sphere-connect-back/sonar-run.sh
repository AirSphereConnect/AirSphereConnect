#!/bin/bash
./mvnw clean verify sonar:sonar -Dmaven.test.failure.ignore=true -Dsonar.projectKey=air-sphere-connect-test -Dsonar.projectName="air-sphere-connect-test" -Dsonar.host.url=http://localhost:9000 -Dsonar.token=sqp_8754bd04763426a4e07b1b677da62673f8ac277f
