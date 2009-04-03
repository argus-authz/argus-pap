#!/bin/bash

# This script sets up the environment tu run the pap service.
# 
# Optional, the environment variable PAP_STANDALONE_VM_OPTIONS may be used
# to pass arguments to the JVM used to execute the service.

# The optional environment variable PAP_STANDALONE_MEM_SIZE defines the size of 
# the heap that will be used by the JVM used to execute the service. Default
# value is 512m.

set -e

# PAP configuration file location
PAP_CONF_FILE="$PAP_HOME/conf/pap_configuration.ini"

# Location of the PAP jars
PAP_LIBS=$PAP_HOME/lib

# Location of the PAP endorsed jars
PAP_ENDORSED_LIBS=$PAP_LIBS/endorsed

# Sets the heap size for the JVM  
if [ -z $PAP_STANDALONE_MEM_SIZE ]; then
	PAP_STANDALONE_MEM_SIZE=512m
fi

# The name of the class that implements the PAP standalone server
PAP_STANDALONE_CLASS="org.glite.authz.pap.server.standalone.PAPServer"

# The name of the class that implements the PAP client
PAP_CLIENT_CLASS="org.glite.authz.pap.ui.cli.PAPCLI"

# The name of the class the implements the PAP shutdown helper
PAP_URL_TOUCHER_CLASS="org.glite.authz.pap.common.utils.URLToucher"

# The trustmanager jar files
TM_DEPS=`ls -x $PAP_LIBS/glite-security-trustmanager-*.jar $PAP_LIBS/glite-security-util-java-*.jar $PAP_LIBS/bcprov-*.jar   | tr '\n' ':'`

# Commons configuration jar files and dependencies 
CONFIG_DEPS=`ls -x $PAP_LIBS/commons-configuration-*.jar $PAP_LIBS/commons-collections-*.jar $PAP_LIBS/commons-lang-*.jar $PAP_LIBS/commons-lang-*.jar $PAP_LIBS/commons-logging-*.jar  | tr '\n' ':'`

# SLF4J Logging jar files and dependencies 
LOGGING_DEPS=`ls -x $PAP_LIBS/slf4j-*.jar $PAP_LIBS/log4j-*.jar $PAP_LIBS/jcl-over-slf4j-*.jar | tr '\n' ':'`

# Jetty jar files and dependencies
JETTY_DEPS=`ls -x $PAP_LIBS/jetty-*.jar $PAP_LIBS/servlet-api-*.jar | tr '\n' ':'`

# ':' separated list of standalone PAP dependencies, used to build the classpath
PAP_DEPS=`ls -x $PAP_LIBS/*.jar | tr '\n' ':'`

# ':' separated list of client PAP dependencies, used to build the classpath
PAP_CLIENT_DEPS=`ls -x $PAP_LIBS/*.jar | grep -v 'glite-security-voms-java-api' | tr '\n' ':'`

# Location of the PAP jar file
PAP_JAR="$PAP_HOME/lib/pap.jar"

# Classpath for the pap client application
PAP_CLIENT_CP="$PAP_DEPS$PAP_JAR:$PAP_HOME/conf/logging/client"

# Classpath for the pap standalone service
PAP_STANDALONE_CP="$TM_DEPS$CONFIG_DEPS$JETTY_DEPS$LOGGING_DEPS$PAP_JAR:$PAP_HOME/conf/logging/standalone"

# Environment for the pap client application
PAP_CLIENT_ENV="-DPAP_HOME=$PAP_HOME -Djava.endorsed.dirs=$PAP_ENDORSED_LIBS"

# Environment for the pap standalone service
PAP_STANDALONE_ENV="-DPAP_HOME=$PAP_HOME -Djava.endorsed.dirs=$PAP_ENDORSED_LIBS -Djava.net.preferIPv4Stack=true"

# Command used to start the pap client application
PAP_CLIENT_CMD="java $PAP_CLIENT_ENV -DconfigureLog4j=false -DeffectiveUserId=$EUID -cp $PAP_CLIENT_CP $PAP_CLIENT_CLASS"

# Command used to start the pap standalone service
PAP_STANDALONE_CMD="java -Xmx$PAP_STANDALONE_MEM_SIZE $PAP_STANDALONE_VM_OPTIONS $PAP_STANDALONE_ENV -cp $PAP_STANDALONE_CP $PAP_STANDALONE_CLASS --conf-dir $PAP_HOME/conf"

# The hostname property as appears in the pap configuration file
PAP_HOST=`grep 'hostname =' $PAP_CONF_FILE | awk '{print $3}'`

# The port property as appears in the pap configuration file
PAP_PORT=`grep 'port =' $PAP_CONF_FILE | awk '{print $3}'`

# The shutdown port property as appears in the pap configuration file
PAP_SHUTDOWN_PORT=`grep 'shutdown_port =' $PAP_CONF_FILE | awk '{print $3}'`

# The certificate property as appears in the pap configuration file
PAP_CERT=`grep 'certificate =' $PAP_CONF_FILE  | awk '{print $3}'`

# The private key property as appears in the pap configuration file
PAP_KEY=`grep 'private_key =' $PAP_CONF_FILE | awk '{print $3}'`

# The command used to shutdown the pap service
PAP_SHUTDOWN_CMD="java -cp $PAP_STANDALONE_CP $PAP_URL_TOUCHER_CLASS http://localhost:$PAP_SHUTDOWN_PORT/shutdown"

# Checks that openssl is installed in the system
check_openssl(){

	sslPath=`which openssl`
	if [ "X$sslPath" = "X" ]; then 
		echo "Error! No openssl found!"; 
		return 1
	fi

}

# Performs sone sanity checks on the certificates 
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



