#!/bin/bash

SHELL_FOLDER=$(dirname $(readlink -f "$0"))
APP_NAME=subscribeDemo-java
HOME_DIR=$SHELL_FOLDER/..

nohup taosBenchmark -f $HOME_DIR/config/insert.json > /dev/null 2>&1 &

taos -s 'create topic subscribedemo as select * from test.meters'

nohup java -Dspring.config.location=$HOME_DIR/config/application.yml -jar $APP_NAME-0.0.1-SNAPSHOT.jar 1> $APP_NAME.log 2>&1 &
