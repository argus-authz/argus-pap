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

import getopt, sys, os, os.path, commands,urllib,xml.dom.minidom, getopt, shutil, time, re, glob

pap_app_name = "glite-authz-pap"
pap_context_name = pap_app_name+".xml"
par_war_name = pap_app_name+".war"

def catalina_conf_dir():
    if not os.environ.has_key('CATALINA_HOME'):
        raise ValueError, "CATALINA_HOME is not defined!"
    
    return os.path.join(os.environ['CATALINA_HOME'],"conf","Catalina","localhost")

def catalina_webapp_dir():
    if not os.environ.has_key('CATALINA_HOME'):
        raise ValueError, "CATALINA_HOME is not defined!"
    
    return os.path.join(os.environ['CATALINA_HOME'],"webapps")

def catalina_webapp_pap_dir():   
    return os.path.join(catalina_webapp_dir(), pap_app_name)

def catalina_pap_war_file():
    return os.path.join(catalina_webapp_dir(), pap_war_name)

def catalina_pap_context_file():
    return os.path.join(catalina_conf_dir(), pap_context_name)

def remove_webapp_pap_war():
    if os.path.exists(catalina_pap_war_file()):
        os.remove(catalina_pap_war_file())
            
def remove_webapp_pap_dir():
    if os.path.exists(catalina_webapp_pap_dir()):
        shutil.rmtree(catalina_webapp_pap_dir(),
                      True)
def pap_war_file():
    if not os.path.exists(os.path.join(os.environ['GLITE_LOCATION'],"share","webapps",pap_war_name)):
        raise RuntimeError,"PAP webapplication not found in usual location: %s." % os.path.join(os.environ['GLITE_LOCATION'],"share","webapps")   
    
    return os.path.join(os.environ['GLITE_LOCATION'],"share","webapps",pap_war_name)

def usage():
    
    usage_str = """
    Usage:
    
    init-pap.py (start|stop|reload|status) 
    
    start deploys the PAP web application on tomcat
    stop undeploys the PAP web application from tomcat
    relead redeploys the PAP web application on tomcat
    status prints a message saying whether the PAP web 
    application is currently deployed on tomcat. 
    """
    print usage_str
    

def parse_cmd_line():   
    global command, vo, options, use_manager
    
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
        

def start():
    shutil.copy(pap_war_file(), catalina_webapp_dir())
    print "The PAP web application has been deployed to tomcat, and it will be soon reachable."

def stop():
    remove_webapp_pap_war()
    remove_webapp_pap_dir()
    print "The PAP web application has been undeployed from tomcat, and it will be soon unreachable."
    

def releoad():
    remove_webapp_pap_dir()
    time.sleep(5)
    start()

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
        if command == "start":
            start()
    
        elif command == "stop":
            stop()
    
        elif command == "restart":
            reload()
    
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