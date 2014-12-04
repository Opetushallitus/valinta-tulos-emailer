#!/bin/sh
pidfile=/var/run/valinta-tulos-emailer.pid
logfile=/var/log/valinta-tulos-emailer.log

if [ -e ${pidfile} ]; then
    pid=`cat ${pidfile}`
    rm ${pidfile}
fi

if [ -z "$pid" ] || ! ps -p ${pid} >&- ; then
    nohup java -server -jar *.jar &> ${logfile} &
    echo $! > ${pidfile}
else
    echo "valinta-tulos-emailer was already running with pid: ${pid}"
fi