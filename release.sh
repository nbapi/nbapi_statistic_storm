#!/bin/bash

## shell正常执行字体颜色
function normal_echo(){
	echo -e '\033[0;35;1m'$1'\033[0m'
}

## shell执行成功字体颜色
function success_echo(){
	echo -e '\033[0;32;1m'$1'\033[0m'
}

## shell执行失败字体颜色
function fail_echo(){
	echo -e '\033[0;31;1m'$1'\033[0m'
}

## 检查自身是否发布
#function checkSelf(){
	
#}

## 检查依赖项是否已发布
function checkDependencies(){
	## 读取依赖项文件
	cat snapshotsMessage.log | while read snapshot; do
		## 获取GroupId ArtifactId Version
		attrArray=(${snapshot//:/ })
		snapshotGroupId=${attrArray[0]}
		snapshotArtifactId=${attrArray[1]}
		snapshotVersion=${attrArray[2]}
		## 查询Maven是否存在Release版本
		mvnCheckUrl="http://mvn.elong.cn:8081/nexus/service/local/lucene/search?g="${snapshotGroupId}"&a="${snapshotArtifactId}"&v="${snapshotVersion}"&collapseresults=true"
		echo $mvnCheckUrl
		mvnCheckResult=`curl $mvnCheckUrl`
		hasRelease=`echo ${mvnCheckResult} | grep -c '<repositoryPolicy>'`
		## 有未发布依赖项,报错且停止发布
		if [ ${hasRelease} -eq 0 ]; then
			fail_echo "发布"${snapshotArtifactId}"失败"
			return 1
		fi
	done
	
	return 0
}

## 修改Snapshot为Release,当发布此模块时,依赖项已全部发布完成,无须检查,将SNAPSHOT依赖项全部置为Release
function modifyPomSnapshotToRelease(){
	## 若SnapshotMessage.log不存在
	if [ ! -f snapshotsMessage.log ]; then
                return 0
        fi	

	## 若SnapshotMessage.log为空
        if [ ! -s snapshotsMessage.log ]; then
                return 0
        fi

	## 读取依赖项文件
	cat snapshotsMessage.log | while read snapshot; do
		## 获取GroupId ArtifactId Version
		attrArray=(${snapshot//:/ })
		snapshotGroupId=${attrArray[0]}
		snapshotArtifactId=${attrArray[1]}
		snapshotVersion=${attrArray[2]}
		## 替换snapshot版本为release,如:1.0.0-snapshot变为1.0.0
		grep -n '.*-SNAPSHOT.*' ./pom.xml | sed 's/:.*//g' | tail -n +2 > snapshotsLineNumber.log
		cat snapshotsLineNumber.log
		awk 'BEGIN{read=1}
		 {if(read){getline a<"snapshotsLineNumber.log";}
		 if(NR==a){read=1;sub(/-SNAPSHOT/,"",$0);}
		 else{read=0};print > "pom.xml.release"}' pom.xml	
		rm -rf snapshotsLineNumber.log
		cat pom.xml.release
		mv pom.xml.release pom.xml
	done
	
	## 提交修改
	git add pom.xml
	
	git commit -m "modify dependencies from snapshot to release."
	
	git push -u origin $1
	
	return 0
}

## 备份环境
function backupEnv(){
	cp -rf pom.xml pom.xml.bak
}

## 回滚环境
function rollbackEnv(){
	mv pom.xml.bak pom.xml
	rm -rf pom.xml.releaseBackup release.properties
}

## 提交环境
function commitEnv(){
	rm -rf pom.xml.bak
}

################################### main #######################################
##大写转小写
typeset -l department

version=${RELEASEVERSION}
artifact_Id=${MODULE}
department=${DEPARTMENT}
echo department:$department
echo artifact_Id:$artifact_Id
echo version:$version

cd `dirname $0`
cur_path=`pwd`

## 切换至迭代分支
git checkout $1
## 更新最新代码并进行merge
git pull

## 获取SNAPSHOT数量,如果等于0表示已发布完成
snapshots=`grep -c '.*-SNAPSHOT.*' ./pom.xml`
if [ $snapshots -eq 0 ]; then
	success_echo "[INFO] 模块已发布成功,无需再次发布."
	exit 0
fi

## 备份环境
backupEnv

## 检查依赖项是否已发布
checkDependencies
if [ $? -ne 0 ]; then
	rollbackEnv
	exit 1
fi

## 修改pom.xml依赖项的-SNAPSHOT
modifyPomSnapshotToRelease ${version}
if [ $? -ne 0 ]; then
	rollbackEnv
	exit 1
fi

## mvn release时,developmentVersion与当前版本相同
mvn release:clean release:prepare -DautoVersionSubmodules=true -DreleaseVersion=${version} -DdevelopmentVersion=${version}-SNAPSHOT -Dtag=${artifact_Id}_${version} -Darguments="-DskipTests"
prepare_release_status=$?
if [ $prepare_release_status -eq 0 ]; then
	## 执行发布
	mvn release:perform  -Darguments="-DskipTests"
	perform_release_status=$?
	if [ $perform_release_status -eq 0 ]; then
		## 手动修改pom.xml中的版本号,去掉-SNAPSHOT
		sed -i "s/${version}-SNAPSHOT/${version}/g" pom.xml
		git add pom.xml
		git commit -m "modify the snapshot version to release version."
		git push -u origin
		## 提交环境修改
		commitEnv
#		jenkins_jobname=${department}_${artifact_Id}-${version}
#		status_code=`curl -w %{http_code} -d "" http://192.168.9.11/job/${jenkins_jobname}/doDelete --user bin.song:zxj,1010`
#		if [ $status_code -eq 302 ]; then
 #       		success_echo "success to delete the jenkins job!!!"$jenkins_jobname
#			exit 0
#		else
 #       		fail_echo "fail to delete the jenkins job, please check ..."$jenkins_jobname
#			exit 0
#		fi
		exit 0
	else
		## 发布出现异常,执行回滚操作
		mvn release:rollback
		## 回滚环境修改
		rollbackEnv
		exit 1
	fi
else
	## 发布出现异常,执行回滚操作
	mvn release:rollback
	## 回滚环境修改
	rollbackEnv
	exit 1
fi

