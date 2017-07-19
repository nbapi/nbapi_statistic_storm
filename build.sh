#!/bin/bash

function normal_echo(){
echo -e '\033[0;35;1m'$1'\033[0m'
}

function success_echo(){
echo -e '\033[0;32;1m'$1'\033[0m'
}

function fail_echo(){
echo -e '\033[0;31;1m'$1'\033[0m'
}

mvn_proj=`pwd`
normal_echo $mvn_proj
svn update
normal_echo "==> svn update start"
svn_update_status=$?
if [ $svn_update_status -eq 0 ]; then
success_echo "==> svn update success"
else
fail_echo "==> svn update fail"
read -n 1 -p "Press any key to continue..."
exit 1
fi

##mvn compile
#normal_echo "==> mvn compile start"
#mvn_compile_status=$?
#if [ $mvn_compile_status -eq 0 ];then
#success_echo "==> mvn compile success"
#else
#fail_echo "==> mvn compile fail"
#read -n 1 -p "Press any key to continue..."
#exit 1
#fi
#
##copy conf to classes
#normal_echo "==> copy conf to classes"
#cp -rf $mvn_proj/src/main/config/* $mvn_proj/target/classes/conf
#copy_conf_status=$?
#if [ $copy_conf_status -eq 0 ];then
#success_echo "==> copy conf success"
#else
#fail_echo "==> copy conf fail"
#read -n 1 -p "Press any key to continue..."
#exit 1
#fi

#mvn package
normal_echo "==> mvn package start"
mvn clean package -Pdebug
mvn_package_status=$?
if [ $mvn_package_status -eq 0 ];then
success_echo "==> mvn package success"
else
fail_echo "==> mvn package fail"
read -n 1 -p "Press any key to continue..."
exit 1
fi

#mvn install
# mvn deploy

# svn commit comment function
function comment_dialog {
	#参数-n的作用是不换行，echo默认是换行 
	echo -n "Please input the svn commit comment:"  
	#从键盘输入  
	read  comment 
	#echo $comment
	#显示信息                  
	count=`expr length "$comment"`

	if [ $count -lt 10 ] ; then
	   echo "the comment is less than 10, please retry."
	   comment_dialog
	fi
}

comment_dialog

#输入注释内容
svn ci -m "$comment" $mvn_proj
svn_commit_status=$?
if [ $svn_commit_status -eq 0 ];then
#echo "==> svn commit success"
success_echo "==> svn commit success"
else
#echo "==> svn commit fail"
fail_echo "==> svn commit fail"
read -n 1 -p "Press any key to exit..."
exit 1
fi

#echo "==> SUCCESS, congratulation!"
success_echo "==> SUCCESS, congratulation!"
read -n 1 -p "Press any key to exit..."
exit 0
