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

set -e

PAP_HOME=usr/share/argus/pap

prefix='../../../..'
bin_prefix="$prefix/.."

# conf
ln -s $prefix/etc/argus/pap $PAP_HOME/conf

# lib
ln -s $prefix/var/lib/argus/pap/lib $PAP_HOME/lib

# logs
ln -s $prefix/var/log/argus/pap $PAP_HOME/logs

# repository
ln -s $prefix/var/lib/argus/pap/repository $PAP_HOME/repository

# doc
ln -s $prefix/usr/share/doc/argus/pap/ $PAP_HOME/doc

# papctl
ln -s ../../$PAP_HOME/sbin/papctl usr/sbin/papctl
ln -s ../../../$PAP_HOME/sbin/papctl etc/rc.d/init.d/argus-pap

# pap-admin
ln -s ../../$PAP_HOME/bin/pap-admin usr/bin/pap-admin


