#!/bin/bash

# This scripts runs the pap-admin command line client

if [ -z $PAP_HOME ]; then
	echo "Please define the PAP_HOME environment variable before running this command!"
	exit 1
fi

ENV_INI_FILE="$PAP_HOME/bin/pap-client-env.sh"

if [ -r $ENV_INI_FILE ]; then
	. $ENV_INI_FILE
else
	echo "Environment initialization file '$ENV_INI_FILE' not found! Check your installation!"
	exit 1
fi

$PAP_CLIENT_CMD "$@"