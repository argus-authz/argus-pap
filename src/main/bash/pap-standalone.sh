#!/bin/bash
set -e
prog=pap-standalone

PAP_RUN_FILE=$GLITE_LOCATION_VAR/lock/subsys/pap-standalone.pid

. $GLITE_LOCATION/etc/pap/sh/pap-utils.sh


pre_checks(){
	check_openssl
	check_certificates
}

success () {
	if [ ! -z "$1" ]; then
		echo "$1"
	fi
	exit 0
}

failure () {
	if [ ! -z "$1" ]; then
		echo "$1"
	fi
	exit 1
}

pap_pid(){

	if test ! -f $PAP_RUN_FILE; then
		echo "No pid file found for $prog!"
		failure
	fi
	
	PAP_PID=`head -n 1 $PAP_RUN_FILE`
	
}

kill_pap_proc(){

	status
	if [ $? -eq 0 ]; then
		## pap process is running
		pid=`head -1 $PAP_RUN_FILE`
		kill -TERM $pid
		if [ $? -ne 0 ]; then
			failure "Error killing PAP process!"
		else
			## remove pid file
			rm $PAP_RUN_FILE
		fi
	else
		failure "PAP standalone server is not running!"
	fi 
}
	
status(){

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
		return 1
	fi
}

alive_and_kicking(){

	$GLITE_LOCATION/bin/pap-admin ping -host $PAP_HOST -port $PAP_PORT -cert $PAP_CERT -key $PAP_KEY >/dev/null 2>&1
	if [ $? -eq 0 ]; then
		echo "PAP alive and kicking responded to service ping request!"
		return 0
	else
		echo "PAP did not respond to service ping request!"
		return 1
	fi
}

start(){

	echo -n "Starting $prog: "
		
	pre_checks || failure 
	
	status && failure "PAP already running..."
	
	$PAP_STANDALONE_CMD &
	
	if [ $? -eq 0 ]; then
		echo "$!" > $PAP_RUN_FILE;
		status || failure "PAP not running after being just started!"
	 	success "Ok."
	else
		failure "failed!"
	fi
		
}

stop(){
	echo -n "Stopping $prog: "
	kill_pap_proc && success "Ok." || failure "Error killing PAP process!"
	rm -f $PAP_RUN_FILE
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
		stop
		start
		;;

	*)
		echo "Usage: $0 {start|stop|status|restart}"
		RETVAL=1
		;;
esac
exit $RETVAL