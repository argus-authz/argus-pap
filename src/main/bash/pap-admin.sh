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

if [ -z $PAP_HOME ]; then
	PAP_HOME="$(cd "${0%/*}/.." && pwd)"
fi

ENV_INI_FILE="$PAP_HOME/lib/pap-client-env.sh"

if [ -r $ENV_INI_FILE ]; then
	. $ENV_INI_FILE
else
	echo "Environment initialization file '$ENV_INI_FILE' not found! Check your installation!"
	exit 1
fi

$PAP_CLIENT_CMD "$@"