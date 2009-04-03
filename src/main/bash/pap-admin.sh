#!/bin/bash

# This scripts runs the pap-admin command line client

if [ -z $PAP_HOME ]; then
	echo "Please define the PAP_HOME environment variable before running this command!"
	exit 1
fi
	

. $PAP_HOME/bin/pap-env.sh

$PAP_CLIENT_CMD "$@"