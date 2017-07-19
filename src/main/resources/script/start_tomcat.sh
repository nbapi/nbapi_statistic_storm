#!/bin/bash
#####SET_VER#####
BASEDIR={#basedir#}
SER_NAME={#name#}
SHUTDOWN_PORT={#tomcatport_shutdown#}
HTTP_PORT={#tomcatport#}
AJP_PORT={#tomcatport_ajp#}
SER_BASE_R_TMP=${BASEDIR}/${SER_NAME}
SER_BASE_R="${SER_BASE_R_TMP//\//\\/}"
#####NO_NEED_2_SET#####
TOMCAT_NAME=tomcat_$HTTP_PORT
SER_BASE=$BASEDIR/$SER_NAME
SRCFILE=/home/work/tmp/template

#####START#####
function init_env
{
	#####START#####
	echo start
	if [ ! -f "$SRCFILE.zip" ]; then 
	 echo "can't find $SRCFILE.zip"
	 return $?
	fi
	echo mkdir $BASEDIR
	mkdir -p $BASEDIR
	cd $BASEDIR
	echo mkdir $BASEDIR/$SER_NAME
	mkdir -p $SER_NAME
	if [ ! -d "$BASEDIR/$TOMCAT_NAME" ]; then 
		echo unzip $SRCFILE 2 $SER_NAME
		unzip -n $SRCFILE -d $SER_NAME > /dev/null
		chmod 777 -R $SER_NAME/tomcat/*
		chmod 777 -R $SER_NAME/java/*
		cd $SER_NAME
		echo rename tomcat 2 $TOMCAT_NAME
		mv tomcat $TOMCAT_NAME
		cd $TOMCAT_NAME/conf
		# copy the server.xml.template to tomcat/conf/server.xml
		mv $SER_BASE/script/server.xml server_default.xml
		#mv server.xml server_default.xml
		echo replace server.xml
		cat server_default.xml| sed 's/{#SHUTDOWN_PORT#}/'$SHUTDOWN_PORT'/' | sed 's/{#HTTP_PORT#}/'$HTTP_PORT'/' | sed 's/{#AJP_PORT#}/'$AJP_PORT'/' | sed 's/{#APP_BASE#}/'$SER_BASE_R'/' | sed 's/{#PROJECT_NAME#}/'$SER_NAME'/'  > server.xml
		cd $SER_BASE
		echo move tomcat
		mv $TOMCAT_NAME $BASEDIR
	fi
	if [ ! -d "$BASEDIR/java" ]; then
		mv $BASEDIR/$SER_NAME/java $BASEDIR
	fi
	echo end
}

echo start
init_env
mkdir -p $BASEDIR
export JAVA_HOME=$BASEDIR/java
export JRE_HOME=$BASEDIR/java/jre
export PATH=$PATH:$JAVA_HOME/bin
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib/rt.jar
cd $BASEDIR
mkdir -p $SER_NAME
#mv car.war car
cd $SER_NAME
jar -xvf "$SER_NAME.war"
#unalias cp
cp -rf "$BASEDIR/$SER_NAME/conf" "$BASEDIR/$SER_NAME/WEB-INF/classes/"
rm "$SER_NAME.war"
rm -rf server.xml
$BASEDIR/$TOMCAT_NAME/bin/start_tomcat.sh &>/dev/null
#/home/work/car/tomcat/bin/startup.sh
exit $?
