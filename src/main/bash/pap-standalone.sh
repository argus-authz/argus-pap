#!/bin/bash
set -e
prog=pap-standalone

PAP_RUN_FILE=$GLITE_LOCATION_VAR/lock/subsys/pap-standalone.pid

#PAP_LOCK_DIR=/var/lock/subsys
#PAP_LOCK_FILE=$PAP_LOCK_DIR/pap-standalone

. $GLITE_LOCATION/etc/pap/sh/pap-utils.sh

success(){
	echo " success!"
	exit 0
}

failure(){
	echo " failure!"
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

	pap_pid
	kill -TERM $PAP_PID
	RETVAL=$?
    [ "$RETVAL" = 0 ] && rm -f $PAP_RUN_FILE;
    
}
start(){

	echo -n "Starting $prog: "
	
	if test -f $PAP_RUN_FILE; then
		echo "already running [`head -n 1 $PAP_RUN_FILE`]!"
		failure
	fi
	
	$PAP_STANDALONE_CMD &
	
	if [ "$?" = "0" ]; then
		echo "$!" > $PAP_RUN_FILE;
	 	success 
	else
		failure
	fi
		
}

stop(){
	echo -n "Stopping $prog: "
	pap_pid
	kill_pap_proc && success || failure
}

case "$1" in
	start)
		start
		;;
	
	stop)
		stop
		;;
	
	status)
		echo "not implemented yet sorry..."
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