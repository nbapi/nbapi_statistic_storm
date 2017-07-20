#!/bin/bash
echo start

cd ／home/work
source .bash_profile
cd /home/work/apache-storm-0.9.6/bin
./storm kill NBAPIStatisticTopology -w 30
exit $?
