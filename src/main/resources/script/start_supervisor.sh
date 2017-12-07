#!/bin/bash
#####set environment variable#####
PATH=$PATH:$HOME/bin
export JAVA_HOME=/home/work/northbound/java
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

#####start supervisor#####
cd /home/work/apache-storm-0.9.6/bin
echo "cd /home/work/apache-storm-0.9.6/bin done"
./storm supervisor >/dev/null 2>&1 & 
echo "./storm supervisor >/dev/null 2>&1 &  done"
result=$?
if [[ result==0 ]]; then
	echo supervisor is start success,result = ${result}
else
	echo supervisor is start failure,result = ${result}
fi
exit ${result}
