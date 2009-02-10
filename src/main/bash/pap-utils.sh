#!/bin/bash
set -e

PAP_LIBS=$GLITE_LOCATION/share/pap/lib

PAP_STANDALONE_CLASS="org.glite.authz.pap.server.PAPServer"
PAP_CLIENT_CLASS="org.glite.authz.pap.ui.cli.PAPCLI"

PAP_DEPS=`ls -x $PAP_LIBS/*.jar | tr '\n' ':'`
PAP_CLIENT_DEPS=`ls -x $PAP_LIBS/*.jar | grep -v 'glite-security-voms-java-api' | tr '\n' ':'`

PAP_JAR="$GLITE_LOCATION/share/java/glite-authz-pap.jar"

PAP_CLIENT_CP="$PAP_DEPS$PAP_JAR:$GLITE_LOCATION/etc/pap/logging/client"
PAP_STANDALONE_CP="$PAP_DEPS$PAP_JAR:$GLITE_LOCATION/etc/pap/logging/standalone"

PAP_CLIENT_ENV="-DGLITE_LOCATION=$GLITE_LOCATION -DGLITE_LOCATION_VAR=$GLITE_LOCATION_VAR"
PAP_STANDALONE_ENV="-DGLITE_LOCATION=$GLITE_LOCATION -DGLITE_LOCATION_VAR=$GLITE_LOCATION_VAR"

# Uncomment the line below to enable remote standalone debugging
# PAP_STANDALONE_VM_OPTIONS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=9876,server=y,suspend=y"

PAP_CLIENT_CMD="java $PAP_CLIENT_ENV -DconfigureLog4j=false -cp $PAP_CLIENT_CP $PAP_CLIENT_CLASS"
PAP_STANDALONE_CMD="java $PAP_STANDALONE_VM_OPTIONS $PAP_STANDALONE_ENV -cp $PAP_STANDALONE_CP $PAP_STANDALONE_CLASS"

PAP_HOST=`grep 'host =' $GLITE_LOCATION/etc/pap/pap_configuration.ini | awk '{print $3}'`
PAP_PORT=`grep 'port =' $GLITE_LOCATION/etc/pap/pap_configuration.ini | awk '{print $3}'`

PAP_CERT=`grep 'sslCertFile' $GLITE_LOCATION/etc/pap/pap_configuration.ini | awk '{print $3}'`
PAP_KEY=`grep 'sslKey' $GLITE_LOCATION/etc/pap/pap_configuration.ini | awk '{print $3}'`

check_openssl(){

	sslPath=`which openssl`
	if [ "X$sslPath" = "X" ]; then 
		echo "Error! No openssl found!"; 
		return 1
	fi

}

check_certificates(){
 	
 	if [ ! -r $PAP_CERT ]; then 
 		echo "PAP service certificate not found or not readable: $PAP_CERT"
 		return 1
 	fi
 	
 	
	if [ ! -r $PAP_KEY ]; then
		echo "PAP service private key not found or not readable: $PAP_KEY" 
		return 1
	fi
		 
	openssl verify -CApath /etc/grid-security/certificates $PAP_CERT >/dev/null 2>&1
	
	if [ $? -ne 0 ]; then 
		echo "OpenSSL verification of service certificate failed!"
		return 1
	fi  

}



