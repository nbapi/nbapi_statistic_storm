#!/bin/bash
#####kill old processid####
ids=`ps -ef|grep java|awk '{print $2 $8}'|grep java|awk -F '/' '{print $1}'`
for processid in ${ids}; do
	echo the process $processid begin to kill
	kill -9  $processid
	killresult=$?
	if [[ killresult==0 ]]; then
		echo the process $processid is killed success,result = ${killresult}	
	else
		echo the process $processid is killed failure,result = ${killresult}	
	fi	
done
echo all processid is killed success.

cd /home/work/apache-storm-0.9.6/bin/storm-local/
rm -rf *
echo storm-local-dir was cleared.
exit 0
