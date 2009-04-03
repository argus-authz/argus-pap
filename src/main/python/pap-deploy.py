#!/usr/bin/python

#############################################################################
# Copyright (c) Members of the EGEE Collaboration. 2009.
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

import getopt,sys, os, os.path, commands,shutil, time, re, glob, string

pap_app_name = "pap"
pap_context_name  = "%s.xml" % pap_app_name
pap_war_name = "%s.war" % pap_app_name
pap_jar_name = "%s.jar" % pap_app_name 

pap_home = os.environ.get("PAP_HOME","/opt/authz/pap")
pap_conf_dir = os.path.join(pap_home,"conf")
pap_jar_dir = os.path.join(pap_home, "lib")
pap_war_dir = os.path.join(pap_home, "wars")

pap_dependencies = glob.glob(pap_jar_dir+"/*.jar")

pap_jar = os.path.join(pap_jar_dir,pap_jar_name)
pap_war = os.path.join(pap_war_dir, pap_war_name)

pap_client_class = "org.glite.authz.pap.ui.cli.PAPCLI"
pap_standalone_class = "org.glite.authz.pap.server.standalone.PAPServer"

pap_client_classes = os.path.join(pap_conf_dir,"logging","client")
pap_standalone_classes = os.path.join(pap_conf_dir,"logging","standalone")

env_variables = ['PAP_HOME']

def pap_war_file():
    if not os.path.exists(pap_war):
        raise RuntimeError,"PAP webapplication not found in usual location: %s." % pap_war   
    
    return pap_war

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


def _build_classpath(classes):
    if len(pap_dependencies) == 0:
        raise RuntimeError, "PAP jar files not found at usual location: %s" % (pap_jar_dir)
    
    pap_dependencies.append(pap_jar)
    pap_dependencies.append(classes)
    
    return string.join(pap_dependencies,":")

def build_standalone_classpath():
    return _build_classpath(pap_standalone_classes)


def build_client_classpath():
    return _build_classpath(pap_client_classes)


def build_env_vars_string():
    return string.join(map(lambda x: "-D%s=%s" % (x, os.environ[x]), env_variables)," ")


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