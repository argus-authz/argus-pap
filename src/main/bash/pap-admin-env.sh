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


# This script sets up the environment to run the pap admin CLI.
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

if [ -r $PAP_HOME/conf/pap_configuration.ini ]; then
	PAP_HOST=`grep 'hostname =' $PAP_HOME/conf/pap_configuration.ini | awk '{print $3}'`
fi

# Command used to start the pap client application
PAP_CLIENT_CMD="java $PAP_CLIENT_ENV -DeffectiveUserId=$EUID -DpapHost=$PAP_HOST -cp $PAP_CLIENT_CP $PAP_CLIENT_CLASS"

