#!/bin/sh
enableProxy=$1
proxyIP=$2
proxyFlushInterval=$3
applicationName=$4
loadgenFlushInterval=$5
java -jar ./shopping/target/shopping-1.0-SNAPSHOT.jar "$enableProxy" "$proxyIP" "$proxyFlushInterval" "$applicationName" server ./shopping/app.yaml &
java -jar ./styling/target/styling-1.0-SNAPSHOT.jar "$enableProxy" "$proxyIP" "$proxyFlushInterval" "$applicationName" server ./styling/app.yaml &
java -jar ./delivery/target/delivery-1.0-SNAPSHOT.jar "$enableProxy" "$proxyIP" "$proxyFlushInterval" "$applicationName" server ./delivery/app.yaml &
./loadgen.sh "$loadgenFlushInterval"
