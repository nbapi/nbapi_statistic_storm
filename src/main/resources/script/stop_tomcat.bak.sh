#!/bin/bash
chmod 777 {#basedir#}/{#name#}/script/init_env.sh {#basedir#}/{#name#}/script/param.sh
#####SET_VER#####
source {#basedir#}/{#name#}/script/param.sh
#####START#####
echo start
if [ ! -d "$BASEDIR/$TOMCAT_NAME" ]; then
 exit $?
fi
$BASEDIR/$TOMCAT_NAME/bin/stop_tomcat.sh
exit $?
