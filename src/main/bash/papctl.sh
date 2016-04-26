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
### BEGIN INIT INFO
# Provides:          argus-pap
# Required-Start:    $network $remote_fs
# Required-Stop:     $network $remote_fs
# Default-Start:     3 4 5
# Default-Stop:      0 1 6
# Short-Description: The Argus PAP
# Description:       The Argus Policy Administration Point service
### END INIT INFO
#
### Chkconfig section
#
# chkconfig: 345 97 97
# description: Argus PAP startup script
# processname: argus-pap
###
#
set -e

prog="$(basename $0)"

PAP_DEFAULTS=/etc/sysconfig/argus-pap

if [ -r $PAP_DEFAULTS ]; then
	source $PAP_DEFAULTS
fi

if [ -z $PAP_HOME ]; then
	PAP_HOME="$(cd "${0%/*}/.." && pwd)"
fi

if [ -z $PAP_RUN_FILE ]; then
	PAP_RUN_FILE=$PAP_HOME/.argus-pap.pid
fi


function pre_checks(){
	check_openssl
	check_certificates
}

function _load_env() {
	. $PAP_HOME/sbin/pap-env.sh
}

function _check_root_user() {
    if [ `id -u` -ne 0 ]; then
        echo "ERROR: You need root privileges to run this command"
        exit 1
    fi
}

function success () {
	if [ ! -z "$1" ]; then
		echo "$1"
	fi
	exit 0
}

function failure () {
	if [ ! -z "$1" ]; then
		echo "$1"
	fi
	exit 1
}

function pap_pid(){

	if test ! -f $PAP_RUN_FILE; then
		echo "No pid file found for $prog!"
		failure
	fi

	PAP_PID=`head -n 1 $PAP_RUN_FILE`

}

function kill_pap_proc(){
	status
	if [ $? -eq 0 ]; then

		_load_env

		## pap process is running
		pid=`head -1 $PAP_RUN_FILE`

		## Use shutdown service hook
		$PAP_SHUTDOWN_CMD

		if [ $? -ne 0 ]; then
			echo "Error shutting down PAP service! Will kill the process..."

			kill -9 $pid

			if [ $? -ne 0 ]; then
				failure "Error killing the PAP service... maybe you don't have the permissions to kill it!"
			else
				## remove pid file
				rm $PAP_RUN_FILE
			fi

		else
			# Still running? Force kill
			if [ `ps --pid $pid | grep -c $pid` -eq 1 ]; then
				kill -KILL $pid
			fi

			## remove pid file
			rm $PAP_RUN_FILE
		fi
	else
		failure "PAP  server is not running!"
	fi
}

function status(){

	if [ -f $PAP_RUN_FILE ]; then
		pid=`head -1 $PAP_RUN_FILE`
		ps -p $pid >/dev/null 2>&1

		if [ $? -ne 0 ]; then
			echo "PAP not running...removing stale pid file"
			rm $PAP_RUN_FILE
			return 1
		else
			return 0
		fi
	else
		## No PID file found, check that there is no PAP running
		## without PID...
		pid=`ps -efww | grep 'java.*PAPServer' | grep -v grep | awk '{print $2}'`
		if [ -z $pid ]; then
			return 1
		else
			echo "A PAP is running, but no pid file found! Will recreate the pid file"
			echo $pid > $PAP_RUN_FILE
			if [ $? -ne 0 ]; then
				failure "Error creating pid file for running PAP!"
			fi
			return 0
		fi
	fi
}


function alive_and_kicking(){
	_load_env

	$PAP_HOME/bin/pap-admin ping -host $PAP_HOST -port $PAP_PORT -cert $PAP_CERT -key $PAP_KEY >/dev/null 2>&1
	if [ $? -eq 0 ]; then
		echo "PAP alive and kicking responded to service ping request!"
		return 0
	else
		echo "PAP did not respond to service ping request!"
		return 1
	fi
}

function start(){
	_check_root_user
	_load_env

	# echo -n "Starting $prog: "

	status && failure "PAP already running..."

	$PAP_CMD &

	if [ $? -eq 0 ]; then
		echo "$!" > $PAP_RUN_FILE;
		status || failure "PAP not running after being just started!"
	 	success
	else
		failure "failed!"
	fi

}

function restart(){
	_check_root_user
	_load_env

	# echo -n "Restarting $prog: "
	kill_pap_proc && (rm -f $PAP_RUN_FILE; sleep 5; start) || failure "Error restarting pap process!"

}

function stop(){
	_check_root_user
	_load_env

	# echo -n "Stopping $prog: "
	kill_pap_proc && (rm -f $PAP_RUN_FILE; success "Ok.") || failure "Error killing PAP process!"
}

case "$1" in
	start)
		start
		;;

	stop)
		stop
		;;

	status)
		status && success "PAP running!" || failure "PAP not running!"
		;;

	restart)
		restart
		;;
	*)
		echo "Usage: $0 {start|stop|status}"
		RETVAL=1
		;;
esac
exit $RETVAL