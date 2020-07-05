#!/bin/sh
enableProxy=$1
DICluster=
DIToken=$2
batchSize=$3
queueSize=$4
DIflushInteval=$5
applicationName=$6
loadgenFlushInterval=$7
java -jar ./shopping/target/shopping-1.0-SNAPSHOT.jar "$enableProxy" "$DICluster" "$DIToken" "$batchSize" "$queueSize" "$DIflushInteval" "$applicationName" server ./shopping/app.yaml &
java -jar ./styling/target/styling-1.0-SNAPSHOT.jar "$enableProxy" "$DICluster" "$DIToken" "$batchSize" "$queueSize" "$DIflushInteval" "$applicationName" server ./styling/app.yaml &
java -jar ./delivery/target/delivery-1.0-SNAPSHOT.jar "$enableProxy" "$DICluster" $DIToken "$batchSize" "$queueSize" "$DIflushInteval" "$applicationName" server ./delivery/app.yaml &
./loadgen.sh "$loadgenFlushInterval"
