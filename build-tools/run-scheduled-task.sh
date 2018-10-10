#!/bin/bash -e

BASEPATH="/root"
CONFIGPATH="/etc/oph-environment"
VARS="${CONFIGPATH}/opintopolku.yml"
LOGPATH="${CONFIGPATH}/log/"

aws s3 cp s3://${ENV_BUCKET}/services/latest/ ${CONFIGPATH}/ --recursive --exclude "templates/*"

echo "Copying resources ..."
cp -vr ${CONFIGPATH}/* ${BASEPATH}/oph-configuration/

echo "Processing configuration files..."
for tpl in `find ${BASEPATH}/ -name "*.template"`
do
  target=`echo ${tpl} | sed "s/\.template//g"`
  echo "Prosessing ${tpl} -> ${target}"
  j2 ${tpl} ${VARS} > ${target}
  chmod 0755 ${target}
done

export LC_CTYPE=fi_FI.UTF-8
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8'
mkdir -p /root/logs

echo "Using java options: ${JAVA_OPTS}"

echo "Starting stantalone-task application...."

export HOME="/root"
export LOGS="${HOME}/logs"
export STANDALONE_JAR=${HOME}/${NAME}.jar

JAVA_OPTS="$JAVA_OPTS -Duser.home=${HOME}"
JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/urandom"
JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"
JAVA_OPTS="$JAVA_OPTS -Dlogback.access=${LOGPATH}/logback-access.xml"
JAVA_OPTS="$JAVA_OPTS -Dlogbackaccess.configurationFile=${LOGPATH}/logback-access.xml"
JAVA_OPTS="$JAVA_OPTS -Dlogback.configurationFile=${LOGPATH}/logback-standalone.xml"
JAVA_OPTS="$JAVA_OPTS -Xloggc:${LOGS}/${NAME}_gc.log"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
JAVA_OPTS="$JAVA_OPTS -XX:+UseGCLogFileRotation"
JAVA_OPTS="$JAVA_OPTS -XX:NumberOfGCLogFiles=10"
JAVA_OPTS="$JAVA_OPTS -XX:GCLogFileSize=10m"
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=${LOGS}/${NAME}_heap_dump-`date +%Y-%m-%d`.hprof"
JAVA_OPTS="$JAVA_OPTS -XX:ErrorFile=${LOGS}/${NAME}_hs_err.log"
JAVA_OPTS="$JAVA_OPTS -D${NAME}.properties=${HOME}/oph-configuration/${NAME}.properties"
echo "java ${JAVA_OPTS} -jar ${STANDALONE_JAR}" > /root/java-cmd.txt
java ${JAVA_OPTS} -jar ${STANDALONE_JAR}
