#/bin/bash

SCRIPT_NAME="client-pap-pdp"

STR=`mvn dependency:build-classpath | grep -A 1 "\[INFO\] Dependencies classpath:"`

CLASSPATH=${STR#\[INFO\] Dependencies classpath:?}

CLASSPATH=$CLASSPATH:`pwd`/target/classes

echo "export CLASSPATH=$CLASSPATH" > $SCRIPT_NAME

echo "" >> SCRIPT_NAME

echo 'java -Dlog4j.configuration=log4j.test.properties -Djava.endorsed.dirs=$JAVA_HOME/jre/lib/endorsed org.glite.authz.pap.test_client.axis.PAPAxisClient "$@"' >> $SCRIPT_NAME

chmod +x $SCRIPT_NAME

