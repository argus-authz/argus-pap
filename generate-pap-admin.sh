#/bin/bash

SCRIPT_NAME="prova-pap-admin"

STR=`mvn dependency:build-classpath | grep -A 1 "\[INFO\] Dependencies classpath:"`

CLASSPATH=${STR#\[INFO\] Dependencies classpath:?}

CLASSPATH=$CLASSPATH:`pwd`/target/classes

echo "export CLASSPATH=$CLASSPATH" > $SCRIPT_NAME

echo 'java -Dlog4j.configuration=log4j.test.properties -Djava.endorsed.dirs=$JAVA_HOME/jre/lib/endorsed org.glite.authz.pap.ui.cli.PAPCLI "$@"' >> $SCRIPT_NAME

chmod +x $SCRIPT_NAME

