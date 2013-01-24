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

# OS dependency flags
[ -z "$PAP_USE_OS_CANL" ] && PAP_USE_OS_CANL="false"
[ -z "$PAP_USE_OS_BC" ] && PAP_USE_OS_BC="false"
[ -z "$PAP_USE_OS_VOMS" ] && PAP_USE_OS_VOMS="false"

# Location of the PAP jars
PAP_LIBS=$PAP_HOME/lib

# Location of the PAP endorsed jars
PAP_ENDORSED_LIBS=$PAP_LIBS/endorsed

# Location of the PAP provided jars
PAP_PROVIDED_LIBS=$PAP_LIBS/provided

# The name of the class that implements the PAP client
PAP_CLIENT_CLASS="org.glite.authz.pap.ui.cli.PAPCLI"

# ':' separated list of client PAP dependencies, used to build the classpath
PAP_CLIENT_DEPS=$(ls -x $PAP_LIBS/*.jar | tr '\n' ':' | sed 's/:$//')

# Include CANL from OS or embedded dir
if [ "$PAP_USE_OS_CANL" = "false" ]; then
    for jar in $PAP_PROVIDED_LIBS/canl-*.jar; do
        [ -f $jar ] && PAP_CLIENT_DEPS="$PAP_CLIENT_DEPS:$jar"
    done
else
    PAP_CLIENT_DEPS="$PAP_CLIENT_DEPS:$OS_JAR_DIR/canl.jar"
fi

# Include BC from OS or embedded dir
if [ "$PAP_USE_OS_BC" = "false" ]; then
    for jar in $PAP_PROVIDED_LIBS/bcmail*.jar $PAP_PROVIDED_LIBS/bcprov*.jar; do
        [ -f $jar ] &&  PAP_CLIENT_DEPS="$PAP_CLIENT_DEPS:$jar"
    done
else
    PAP_CLIENT_DEPS="$PAP_CLIENT_DEPS:$OS_JAR_DIR/bcprov-1.46.jar:$OS_JAR_DIR/bcmail-1.46.jar"
fi

# Include VOMS from OS or embedded dir
if [ "$PAP_USE_OS_VOMS" = "false" ]; then
    for jar in $PAP_PROVIDED_LIBS/voms-api-java-*.jar; do
      [ -f $jar ] && PAP_CLIENT_DEPS="$PAP_CLIENT_DEPS:$jar"
    done
else
    PAP_CLIENT_DEPS="$PAP_CLIENT_DEPS:$OS_JAR_DIR/voms-api-java3.jar"
fi

# Location of the PAP jar file
PAP_JAR="$PAP_HOME/lib/pap.jar"

# Classpath for the pap client application
PAP_CLIENT_CP="$PAP_CLIENT_DEPS:$PAP_HOME/conf/logging/client"

# Environment for the pap admin application
PAP_CLIENT_ENV="-DPAP_HOME=$PAP_HOME -Djava.endorsed.dirs=$PAP_ENDORSED_LIBS"

# Command used to start the pap client application
PAP_CLIENT_CMD="java $PAP_CLIENT_ENV -DwantLog4jSetup=false -DeffectiveUserId=$EUID -cp $PAP_CLIENT_CP $PAP_CLIENT_CLASS"

