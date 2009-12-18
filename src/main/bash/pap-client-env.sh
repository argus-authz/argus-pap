#!/bin/bash

# This script sets up the environment to run the pap client application.
# 
set -e

# Location of the PAP jars
PAP_LIBS=$PAP_HOME/lib

# Location of the PAP endorsed jars
PAP_ENDORSED_LIBS=$PAP_LIBS/endorsed

# The name of the class that implements the PAP client
PAP_CLIENT_CLASS="org.glite.authz.pap.ui.cli.PAPCLI"

# ':' separated list of client PAP dependencies, used to build the classpath
PAP_CLIENT_DEPS=`ls -x $PAP_LIBS/*.jar | grep -v 'voms-api-java' | tr '\n' ':'`

# Location of the PAP jar file
PAP_JAR="$PAP_HOME/lib/pap.jar"

# Classpath for the pap client application
PAP_CLIENT_CP="$PAP_CLIENT_DEPS$PAP_JAR:$PAP_HOME/conf/logging/client"

# Environment for the pap client application
PAP_CLIENT_ENV="-DPAP_HOME=$PAP_HOME -Djava.endorsed.dirs=$PAP_ENDORSED_LIBS"

# Command used to start the pap client application
PAP_CLIENT_CMD="java $PAP_CLIENT_ENV -DconfigureLog4j=false -DeffectiveUserId=$EUID -cp $PAP_CLIENT_CP $PAP_CLIENT_CLASS"