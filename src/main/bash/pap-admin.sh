#!/bin/bash

if [ -z $GLITE_LOCATION ]; then
	echo "Please define the GLITE_LOCATION environment variable before running this command!"
	exit 1
fi
	
if [ -z $GLITE_LOCATION_VAR ]; then
	echo "Please define the GLITE_LOCATION_VAR environment variable before running this command!"
	exit 1
fi

. $GLITE_LOCATION/etc/pap/sh/pap-utils.sh

$PAP_CLIENT_CMD "$@"