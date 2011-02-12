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


# This scripts runs the pap-admin command line client

PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

if [ -r "/etc/sysconfig/argus-pap" ]; then
	source /etc/sysconfig/argus-pap
fi

# Only set PAP_HOME if not already set
[ -z "$PAP_HOME" ] && PAP_HOME=`cd "$PRGDIR/.." ; pwd`

ENV_INI_FILE="$PAP_HOME/bin/pap-admin-env.sh"

if [ -r $ENV_INI_FILE ]; then
	. $ENV_INI_FILE
else
	echo "Environment initialization file '$ENV_INI_FILE' not found! Check your installation!"
	exit 1
fi

$PAP_CLIENT_CMD "$@"