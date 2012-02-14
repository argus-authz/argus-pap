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

create_symlink(){
	if [ -e $2 ]; then
		rm -rf $2
	fi
	
	ln -s $1 $2
}

# conf
create_symlink $prefix/etc/argus/pap $PAP_HOME/conf

# lib
create_symlink $prefix/var/lib/argus/pap/lib $PAP_HOME/lib

# logs
create_symlink $prefix/var/log/argus/pap $PAP_HOME/logs

# repository
create_symlink $prefix/var/lib/argus/pap/repository $PAP_HOME/repository

# doc
create_symlink $prefix/usr/share/doc/argus/pap/ $PAP_HOME/doc

# papctl
create_symlink ../../$PAP_HOME/sbin/papctl usr/sbin/papctl
create_symlink ../../../$PAP_HOME/sbin/papctl etc/rc.d/init.d/argus-pap

# pap-admin
create_symlink ../../$PAP_HOME/bin/pap-admin usr/bin/pap-admin