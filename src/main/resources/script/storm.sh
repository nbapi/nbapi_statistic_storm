#!/bin/bash
echo start

cd ／home/work
source .bash_profile
cd /home/work/apache-storm-0.9.6/bin
./storm kill NBAPIStatisticTopology -w 60
echo "storm kill NBAPIStatisticTopology finished."
./storm jar /home/work/nbapi_statistic_storm/nbapi_statistic_storm-jar-with-dependencies.jar com.elong.hotel.main.TopologyMainCluster
echo "./storm jar /home/work/nbapi_statistic_storm/nbapi_statistic_storm-jar-with-dependencies.jar com.elong.hotel.main.TopologyMainCluster finished."
exit $?