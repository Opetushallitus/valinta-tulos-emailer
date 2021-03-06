#!/bin/sh
JAVA_HOME=/data00/oph/java/jdk1.8.0_60/jre
PATH=$PATH:$JAVA_HOME/bin

user_home=/data00/oph/valinta-tulos-emailer
pidfile=${user_home}/valinta-tulos-emailer.pid
logfile=${user_home}/logs/valinta-tulos-runner.log

if [ -e ${pidfile} ]; then
    pid=`cat ${pidfile}`
fi

if [ -z "$pid" ] || ! ps -p ${pid} >&- ; then
    if [ -e ${pidfile} ]; then
        rm ${pidfile}
    fi
    nohup java -Duser.home=${user_home} -server -jar *.jar &> ${logfile} &
    echo $! > ${pidfile}
else
    echo "valinta-tulos-emailer was already running with pid: ${pid}"
fi
