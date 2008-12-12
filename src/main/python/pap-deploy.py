#!/usr/bin/python

#############################################################################
# Copyright (c) Members of the EGEE Collaboration. 2006.
# See http://www.eu-egee.org/partners/ for details on the copyright
# holders.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Authors:
#     Andrea Ceccanti - andrea.ceccanti@cnaf.infn.it
#############################################################################

import getopt,sys, os, os.path, commands,shutil, time, re, glob
from pap_utils import *

def remove_webapp_pap_war():
    if os.path.exists(catalina_pap_war_file()):
        os.remove(catalina_pap_war_file())
            
def remove_webapp_pap_dir():
    if os.path.exists(catalina_webapp_pap_dir()):
        shutil.rmtree(catalina_webapp_pap_dir(),
                      True)


def usage():
    
    usage_str = """
    Usage:
    
    pap-deploy.py (deploy|undeploy|redeploy|status) 
    
    deploy deploys the PAP web application on tomcat
    undeploy undeploys the PAP web application from tomcat
    redeploy redeploys the PAP web application on tomcat
    status prints a message saying whether the PAP web 
    application is currently deployed on tomcat. 
    """
    print usage_str
    

def parse_cmd_line():   
    global command
    
    if len(sys.argv) == 1:
        print "No command given!"
        usage()
        sys.exit(2)
    
    
    cmd_line = sys.argv[1:]
    
    try:
        
        opts,args = getopt.getopt(cmd_line,"",[]) 
        
        ## No options foreseen now         
        #for k,v in opts:
        #    options[k[2:]]=v
        
        command=args[0]
        
        
    except getopt.GetoptError, m:
        print "Error parsing command line arguments!", m
        usage()
        sys.exit(1)
        

def deploy():
    shutil.copy(pap_war_file(), catalina_webapp_dir())
    print "The PAP web application has been deployed to tomcat, and it will be soon reachable."

def undeploy():
    remove_webapp_pap_war()
    remove_webapp_pap_dir()
    print "The PAP web application has been undeployed from tomcat, and it will be soon unreachable."
    

def redeploy():
    remove_webapp_pap_dir()
    time.sleep(5)
    deploy()

def status():
    if (os.path.exists(catalina_pap_context_file())):
        print "The PAP application is currently deployed on tomcat"
        sys.exit(0)
    else:
        print "The PAP application is currently NOT deployed on tomcat"
        sys.exit(1)

def main():
    parse_cmd_line()
    try:
        if command == "deploy":
            deploy()
    
        elif command == "undeploy":
            undeploy()
    
        elif command == "redeploy":
            redeploy()
    
        elif command == "status":
            status()
    
        else:
            print "Unknown command!"
            usage()
            sys.exit(1)
    
    except RuntimeError, detail:
        print detail
        sys.exit(1)
        

if __name__ == '__main__':
    main()