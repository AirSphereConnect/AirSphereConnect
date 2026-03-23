#!/bin/bash
npm run test:coverage -- --watch=false --browsers=ChromeHeadless && npx sonar-scanner -Dsonar.projectKey=air-sphere-connect-test-front -Dsonar.projectName="Air Sphere Connect Front" -Dsonar.host.url=http://localhost:9000 -Dsonar.token=sqp_281479ba8e3e218593ad5fe2451b47544ebde361

