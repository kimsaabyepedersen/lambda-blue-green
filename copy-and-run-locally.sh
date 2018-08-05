#!/usr/bin/env bash

sh build.sh
cp java/functions/showMyIp/target/showMyIp-1.0-SNAPSHOT.jar build
sam local start-api
