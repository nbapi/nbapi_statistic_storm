#!/bin/bash
#####set environment variable#####
PATH=$PATH:$HOME/bin
export JAVA_HOME=/home/work/northbound/java
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
echo "set environment variable ... done"

#####kill topology#####
cd /home/work/apache-storm-0.9.6/bin
echo "/home/work/apache-storm-0.9.6/bin ... done"
./storm kill NBAPIStatisticTopology -w 60
echo "./storm kill NBAPIStatisticTopology -w 60 ... done"

cd /home/work/apache-storm-0.9.6/bin/storm-local/nimbus/stormdist
echo "cd /home/work/apache-storm-0.9.6/bin/storm-local/nimbus/stormdist ... done"
rm -rf *
echo "rm -rf * ... done"
#####start topology#####
cd /home/work/apache-storm-0.9.6/bin
echo "/home/work/apache-storm-0.9.6/bin ... done"
./storm jar /home/work/nbapi_statistic_storm/nbapi_statistic_storm-jar-with-dependencies.jar com.elong.hotel.main.TopologyMainCluster >/dev/null 2>&1 &
echo "./storm jar /home/work/nbapi_statistic_storm/nbapi_statistic_storm-jar-with-dependencies.jar com.elong.hotel.main.TopologyMainCluster ... done"
exit $?