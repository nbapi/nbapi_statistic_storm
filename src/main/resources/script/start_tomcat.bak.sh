#!/bin/bash
chmod 777 {#basedir#}/{#name#}/script/init_env.sh {#basedir#}/{#name#}/script/param.sh
#####SET_VER#####
source {#basedir#}/{#name#}/script/param.sh
#####START#####
echo start
$BASEDIR/$SER_NAME/script/init_env.sh
mkdir -p $BASEDIR
export JAVA_HOME=$BASEDIR/java
export JRE_HOME=$BASEDIR/java/jre
export PATH=$PATH:$JAVA_HOME/bin
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib/rt.jar
cd $BASEDIR
mkdir -p $SER_NAME
#mv car.war car
cd $SER_NAME
jar -xvf "$SER_NAME.war"
#unalias cp
cp -rf "$BASEDIR/$SER_NAME/conf" "$BASEDIR/$SER_NAME/WEB-INF/classes/"
rm "$SER_NAME.war"
$BASEDIR/$TOMCAT_NAME/bin/start_tomcat.sh &>/dev/null
#/home/work/car/tomcat/bin/startup.sh
exit $?
