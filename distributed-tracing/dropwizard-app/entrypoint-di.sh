#!/bin/sh
enableProxy=$1
DICluster=$2
DIToken=$3
batchSize=$4
queueSize=$5
DIflushInteval=$6
applicationName=$7
loadgenFlushInterval=$8
java -jar ./shopping/target/shopping-1.0-SNAPSHOT.jar "$enableProxy" "$DICluster" "$DIToken" "$batchSize" "$queueSize" "$DIflushInteval" "$applicationName" server ./shopping/app.yaml &
java -jar ./styling/target/styling-1.0-SNAPSHOT.jar "$enableProxy" "$DICluster" "$DIToken" "$batchSize" "$queueSize" "$DIflushInteval" "$applicationName" server ./styling/app.yaml &
java -jar ./delivery/target/delivery-1.0-SNAPSHOT.jar "$enableProxy" "$DICluster" $DIToken "$batchSize" "$queueSize" "$DIflushInteval" "$applicationName" server ./delivery/app.yaml &
./loadgen.sh "$loadgenFlushInterval"
