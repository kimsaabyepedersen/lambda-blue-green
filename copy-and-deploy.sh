#!/usr/bin/env bash

#pushd java/functions/showMyIp
#mvn clean install
#popd
#cp java/functions/showMyIp/target/showMyIp-1.0-SNAPSHOT.jar build
sam package     --template-file template.yaml     --output-template-file packaged.yaml     --s3-bucket  alias-lambda-taffic-shifting
sam deploy     --template-file packaged.yaml     --stack-name aliasShifting     --capabilities CAPABILITY_IAM
