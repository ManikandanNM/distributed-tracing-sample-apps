#!/bin/sh
java -jar ./shopping/target/shopping-1.0-SNAPSHOT.jar cedbf9b5-4313-4ea6-9c0a-b09723a409c2 server ./shopping/app.yaml &
java -jar ./styling/target/styling-1.0-SNAPSHOT.jar cedbf9b5-4313-4ea6-9c0a-b09723a409c2 server ./styling/app.yaml &
java -jar ./delivery/target/delivery-1.0-SNAPSHOT.jar cedbf9b5-4313-4ea6-9c0a-b09723a409c2 server ./delivery/app.yaml &
./loadgen.sh 5
