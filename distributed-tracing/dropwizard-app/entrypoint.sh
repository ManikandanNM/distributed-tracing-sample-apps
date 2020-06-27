#!/bin/sh
enableProxy=$1
proxyIPOrDIToken=$2
applicationName=$3
flushInterval=$4
echo "$enableProxy" >> ./sample.text
echo "$proxyIPOrDIToken" >> ./sample.text
echo "$applicationName" >> ./sample.text
echo "$flushInterval" >> ./sample.text
sleep 300
