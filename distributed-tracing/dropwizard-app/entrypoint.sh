#!/bin/sh
enableProxy=$1
proxyIPOrDIToken=$2
applicationName=$3
flushInterval=$4
java -jar ./shopping/target/shopping-1.0-SNAPSHOT.jar "$enableProxy" "$proxyIPOrDIToken" "$applicationName" server ./shopping/app.yaml &
java -jar ./styling/target/styling-1.0-SNAPSHOT.jar "$enableProxy" "$proxyIPOrDIToken" "$applicationName" server ./styling/app.yaml &
java -jar ./delivery/target/delivery-1.0-SNAPSHOT.jar "$enableProxy" "$proxyIPOrDIToken" "$applicationName" server ./delivery/app.yaml &
./loadgen.sh "$flushInterval"
