#!/bin/bash
echo "start application in production mode"

#export JAVA_HOME=/data/jdk
#export PATH="$JAVA_HOME"/bin:$PATH
#export CLASSPATH=.:"$JAVA_HOME"/lib/dt.jar:"$JAVA_HOME"/lib/tools.jar

base_dir=$(dirname $0)/..

module_name="yhs-service-demo2-test2"

#echo "$base_dir"

# default spring profiles active is 'prod'
if [ -z "$1" ]; then
  SPRING_PROFILES_ACTIVE="prod"
else
  SPRING_PROFILES_ACTIVE=$1
fi

# default jvm heap set prod env value
# -Xms3072M -Xmx3072M
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

PID=$(ps -ef |grep "${module_name}" |grep -v 'grep'|awk '{print $2}')

#echo  $PID

if [ "$PID" ]; then
  echo "Service is running, pid=$PID"
else
  nohup /data/jdk/bin/java -jar $SERVER_HEAP_OPTS -XX:+UseConcMarkSweepGC -verbose:gc -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError -Xloggc:/data/tomcat/logs/gc.log -Djava.security.egd=file:/dev/./urandom -Dcatalina.base=/data/tomcat -Dcatalina.home=/data/tomcat -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9103 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -DBASE_LOG_HOME=/data/tomcat $EXEC_JAR --spring.profiles.active=$SPRING_PROFILES_ACTIVE >>/data/tomcat/logs/catalina.out 2>&1 </dev/null &
  #    nohup java -jar $SERVER_HEAP_OPTS  -XX:+UseConcMarkSweepGC -verbose:gc -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError -Xloggc:/data/tomcat/logs/gc.log -Djava.security.egd=file:/dev/./urandom -Dcatalina.base=/data/tomcat -Dcatalina.home=/data/tomcat -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9103 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -DBASE_LOG_HOME=/data/tomcat $EXEC_JAR --spring.profiles.active=$SPRING_PROFILES_ACTIVE > ./run.out 2>&1 < /dev/null & echo $! > $pid_file
  echo ">>>>>>start successfully!"
fi
