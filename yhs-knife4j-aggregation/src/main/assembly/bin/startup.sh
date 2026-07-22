#!/bin/bash
echo "start application in production mode"

#export JAVA_HOME=/data/jdk
#export PATH="$JAVA_HOME"/bin:$PATH
#export CLASSPATH=.:"$JAVA_HOME"/lib/dt.jar:"$JAVA_HOME"/lib/tools.jar

base_dir=$(dirname $0)/..

module_name="yhs-knife4j-aggregation"
log_dir="/data/tomcat/$module_name"
#echo "$base_dir"

# default spring profiles active is 'prod'
if [ -z "$1" ]; then
  SPRING_PROFILES_ACTIVE="prod"
else
  SPRING_PROFILES_ACTIVE=$1
fi

# default jvm heap set prod env value
# -Xms6144M -Xmx6144M  4核8G配置
if [ -z "$2" ]; then
  SERVER_HEAP_OPTS="-Xms3072M -Xmx3072M"
else
  SERVER_HEAP_OPTS=$2
fi

# default exec jar '$module_name.jar'
if [ -z "$3" ]; then
  EXEC_JAR="$base_dir/$module_name.jar"
else
  EXEC_JAR = $3
fi

#echo "$EXEC_JAR"

#make run dir
mkdir -p "$base_dir"/run
mkdir -p $log_dir

pid_file="$base_dir/run/$module_name-tpid"

tpid=$(ps -aef | grep $module_name | grep -v "grep" | awk '{print $2}')

#echo $pid_file  $tpid

if [ "$tpid" ]; then
  echo "Service is running, pid=$tpid"
else
  nohup /data/jdk/bin/java -jar $SERVER_HEAP_OPTS -XX:+UseG1GC -Xlog:gc*:file=$log_dir/gc.log:time,tags:filecount=10,filesize=100M -Djava.security.egd=file:/dev/urandom -Dcatalina.base=/data/tomcat/$module_name -Dcatalina.home=/data/tomcat/$module_name -DBASE_LOG_HOME=/data/tomcat/$module_name/ $EXEC_JAR --spring.profiles.active=$SPRING_PROFILES_ACTIVE >> $log_dir/catalina.out  2>&1 </dev/null &
  echo $! >$pid_file
  echo ">>>>>>start successfully!"
fi
