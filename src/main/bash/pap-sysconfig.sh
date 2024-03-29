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

## Sets the base of the PAP installation
PAP_HOME=${PAP_HOME}

## The PAP pid file
PAP_RUN_FILE=/var/run/argus-pap.pid

# This variable can be used to pass arguments to the JVM used to execute the service.
#PAP_JAVA_OPTS=

# This variable can be used to define a custom JVM used to run the PAP service and CLI.
#PAP_JAVA=
