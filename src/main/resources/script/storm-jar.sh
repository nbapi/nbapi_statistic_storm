#!/bin/bash
echo start

cd ／home/work
source .bash_profile
cd /home/work/apache-storm-0.9.6/bin
./storm jar nbapi_statistic_storm-jar-with-dependencies.jar com.elong.hotel.main.TopologyMainCluster
exit $?