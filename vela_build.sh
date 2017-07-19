#/bin/bash

####################################################
######          paramer                      #######
####################################################

project_build_version=$version
build_id=$id
branch_version=$codeBranch

job_name=nbapi-nbapi_statistic_storm



####################################################
######          main function                #######
####################################################

status_code=`curl -w %{http_code} -d "project_build_version=$project_build_version&buildjob_id=${build_id}&codeBranch=${branch_version}" http://ci.elong.cn/job/$job_name/buildWithParameters --user bin.song:zxj,1010`

last_build_number=`curl http://ci.elong.cn/job/${job_name}/lastBuild/buildNumber --user bin.song:zxj,1010`
if [[ $last_build_number == *[!0-9]* ]]; then
        last_build_number=0
fi

let last_build_number++

log_path="<a href=\"http://ci.elong.cn/job/"${job_name}"/"${last_build_number}"/console\">"${job_name}"_"${project_build_version}"_buildlog</a>"

if [ "${status_code}" = "201" ]; then
        echo $log_path
        exit -1
else
        exit 1
fi
