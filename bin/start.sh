#!/bin/bash

SHELL_FOLDER=$(dirname $(readlink -f "$0"))
APP_NAME=subscribeDemo-java
JAR=subscribeDemo-java-0.0.1-SNAPSHOT.jar
HOME_DIR=$SHELL_FOLDER/..

PID=$(ps -ef | grep "${JAR}" | grep -v grep | awk '{ print $2 }')
if [[ -z "$PID" ]]
then
    echo -e "${JAR} is already stopped \n"
else
    echo -e "kill  ${PID} \n"
    kill -9 ${PID}
    echo -e "${JAR} stopped successfully \n"
fi

PID=$(ps -ef | grep "taosBenchmark" | grep -v grep | awk '{ print $2 }')
if [[ -z "$PID" ]]
then
    echo -e "taosBenchmark is already stopped \n"
else
    echo -e "kill  ${PID} \n"
    kill -9 ${PID}
    echo -e "taosBenchmark stopped successfully \n"
fi

sleep 3

taos -s 'drop topic if exists subscribedemo'
taos -s 'show topics'

sleep 2

nohup taosBenchmark -f ./config/insert.json  1> taosBenchmark.log  2>&1 &

sleep 2

taos -s 'create topic subscribedemo as select * from test.meters'

sleep 2

nohup java -Dspring.config.location=$HOME_DIR/config/application.yml -jar $APP_NAME-0.0.1-SNAPSHOT.jar 1> $APP_NAME.log 2>&1 &

PID=$(ps -ef | grep "${JAR}" | grep -v grep | awk '{ print $2 }')
if [[ -z "$PID" ]]
then
	echo -e "\n\n ${JAR} cannot start properly!"
else
	echo -e "\n\n ${JAR} is running ..."
fi

PID=$(ps -ef | grep taosBenchmark | grep -v grep | awk '{ print $2 }')
if [[ -z "$PID" ]]
then
	echo -e "\n\n taosBenchmark cannot start properly! \n\n"
else
	echo -e "\n\n taosBenchmark is running ... \n\n"
fi
