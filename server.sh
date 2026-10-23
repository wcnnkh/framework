#! /usr/bin/bash

usage() {
	echo "Usage: sh server.sh [start|stop|restart|status]"
	exit 1
}

is_exist(){
    pid=$(ps -ef | grep $APP_NAME | grep 'java -jar' | grep -v grep | awk '{print $2}')
	if [ -z $pid ]; then
		return "1"
	else
	    echo "${APP_NAME} is running. Pid is ${pid}"
		return "0"
	fi
}

start(){
	is_exist
	if [ $? -eq "0" ]; then
		echo "${APP_NAME} is already running. pid=${pid} ."
	elif [ -z "${PROFILE}" ]; then
		echo "${APP_NAME} is start running"
		nohup java -jar $APP_NAME> /dev/null 2>&1 &	
	else
		echo "${APP_NAME} is start running"
		nohup java -jar $APP_NAME --spring.profiles.active=$PROFILE > /dev/null 2>&1 &
	fi
}

stop(){
	is_exist
	if [ $? -eq "0" ]; then
		kill -9 $pid
	else
		echo "${APP_NAME} is not running"
	fi  
}

status(){
	is_exist
	if [ $? -eq "0" ]; then
		echo "${APP_NAME} is running. Pid is ${pid}"
	else
		echo "${APP_NAME} is NOT running."
	fi
}

restart(){
	stop
	start
}

APP_NAME=$2

if [ -z "${APP_NAME}" ]; then
        echo "require jar name"
        exit 1
fi
PROFILE=$3
pid=''

case "$1" in
	"start")
		start
		;;
	"stop")
		stop
		;;
	"status")
		status
		;;
	"restart")
		restart
		;;
	*)
		usage
		;;
esac
