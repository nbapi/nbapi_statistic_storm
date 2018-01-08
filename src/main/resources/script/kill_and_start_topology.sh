#!/bin/bash
#####set environment variable#####
PATH=$PATH:$HOME/bin
export JAVA_HOME=/home/work/northbound/java
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
echo "set environment variable ... done"

#####kill topology#####
cd /home/work/NBAPI/jstorm-2.1.1/bin
echo "/home/work/NBAPI/jstorm-2.1.1/bin ... done"
./jstorm kill NBAPIStatisticTopologyJstorm 60
echo "./storm kill NBAPIStatisticTopologyJstorm 60 ... done"

cd /home/work/NBAPI/jstorm-2.1.1/data/nimbus/stormdist
echo "cd /home/work/NBAPI/jstorm-2.1.1/data/nimbus/stormdist ... done"
rm -rf *
echo "rm -rf * ... done"
#####start topology#####
cd /home/work/NBAPI/jstorm-2.1.1/bin
echo "cd /home/work/NBAPI/jstorm-2.1.1/bin ... done"
./jstorm jar /home/work/nbapi_statistic_storm/nbapi_statistic_storm-jar-with-dependencies.jar com.elong.hotel.main.TopologyMainCluster >/dev/null 2>&1 &
echo "./jstorm jar /home/work/nbapi_statistic_storm/nbapi_statistic_storm-jar-with-dependencies.jar com.elong.hotel.main.TopologyMainCluster ... done"
exit $?