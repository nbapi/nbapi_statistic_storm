#!/bin/bash
##init_tomcat+java
##by_dan.zhao
#####SET_VER#####
source {#basedir#}/{#name#}/script/param.sh
#####START#####
echo start
if [ ! -f "$SRCFILE.zip" ]; then 
 echo "can't find $SRCFILE.zip"
 exit $?
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
mv server.xml server_default.xml
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
exit $?