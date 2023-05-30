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
