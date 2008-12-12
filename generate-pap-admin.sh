#/bin/bash

SCRIPT_NAME="pap-admin"

STR=`mvn dependency:build-classpath | grep -A 1 "\[INFO\] Dependencies classpath:"`

CLASSPATH=${STR#\[INFO\] Dependencies classpath:?}

CLASSPATH=$CLASSPATH:`pwd`/target/classes:$GLITE_LOCATION/etc/pap/logging/client

echo "export CLASSPATH=$CLASSPATH" > $SCRIPT_NAME

echo 'java -DconfigureLog4j=false org.glite.authz.pap.ui.cli.PAPCLI "$@"' >> $SCRIPT_NAME

chmod +x $SCRIPT_NAME

