#!/bin/bash

# This scripts runs the pap-admin command line client

if [ -z $PAP_HOME ]; then
	PAP_HOME="$(cd "${0%/*}/.." && pwd)"
fi

ENV_INI_FILE="$PAP_HOME/bin/pap-client-env.sh"

if [ -r $ENV_INI_FILE ]; then
	. $ENV_INI_FILE
else
	echo "Environment initialization file '$ENV_INI_FILE' not found! Check your installation!"
	exit 1
fi

$PAP_CLIENT_CMD "$@"