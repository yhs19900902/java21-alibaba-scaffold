#!/bin/bash
echo "shutdown application in production mode"

module_name="yhs-knife4j-aggregation"

base_dir=$(dirname $0)/..

pid_file="$base_dir/run/$module_name-tpid"

#echo $pid_file

tpid=$(cat $pid_file | awk '{print $1}')

tpid=$(ps -aef | grep $tpid | awk '{print $2}' | grep $tpid)
#echo $tpid

if [ ${tpid} ]; then
  kill $tpid
  rm -f $pid_file
  sleep 3
  tpid=$(ps -aef | grep $tpid | awk '{print $2}' | grep $tpid)
  if [ ${tpid} ]; then
    kill -9 $tpid
    sleep 1
    echo "[$module_name] process shutdown by kill -9 $tpid"
  fi
fi

echo ">>>>>>shutdown successfully!"
