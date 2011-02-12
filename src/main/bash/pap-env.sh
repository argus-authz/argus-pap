#!/bin/bash
#
# Copyright (c) Members of the EGEE Collaboration. 2006-2009.
# See http://www.eu-egee.org/partners/ for details on the copyright holders.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


# This script sets up the environment for the PAP service.
# 
# DO NOT CHANGE THE CONTENT OF THIS FILE UNLESS YOU REALLY KNOW WHAT YOU ARE DOING
#

set -e

# Location of the PAP jars
PAP_LIBS=$PAP_HOME/lib

# Location of the PAP endorsed jars
PAP_ENDORSED_LIBS=$PAP_LIBS/endorsed

# PAP configuration file location
PAP_CONF_FILE="$PAP_HOME/conf/pap_configuration.ini"

# Sets the heap size for the JVM  
if [ -z $PAP_JAVA_OPTS ]; then
	PAP_JAVA_OPTS="-Xmx512m "
fi

# The name of the class that implements the PAP standalone server
PAP_CLASS="org.glite.authz.pap.server.standalone.PAPServer"

# The name of the class the implements the PAP shutdown helper
PAP_SHUTDOWN_CLASS="org.glite.authz.pap.server.standalone.ShutdownClient"

# ':' separated list of  PAP dependencies, used to build the classpath
PAP_DEPS=`ls -x $PAP_LIBS/*.jar | tr '\n' ':'`

# Location of the PAP jar file
PAP_JAR="$PAP_HOME/lib/pap.jar"

# Classpath for the pap service
PAP_CP="$PAP_DEPS:$PAP_HOME/conf/logging/standalone"

# Environment for the pap service
PAP_ENV="-DPAP_HOME=$PAP_HOME -Djava.endorsed.dirs=$PAP_ENDORSED_LIBS"

# Command used to start the pap  service
PAP_CMD="java $PAP_JAVA_OPTS $PAP_ENV -cp $PAP_CP $PAP_CLASS --conf-dir $PAP_HOME/conf"

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
PAP_SHUTDOWN_CMD="java -DPAP_HOME=$PAP_HOME -cp $PAP_CP $PAP_SHUTDOWN_CLASS"