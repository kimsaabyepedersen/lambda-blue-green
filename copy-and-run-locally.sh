#!/usr/bin/env bash

pushd java/functions/showMyIp
mvn clean install
popd
cp java/functions/showMyIp/target/showMyIp-1.0-SNAPSHOT.jar build
sam local start-api
