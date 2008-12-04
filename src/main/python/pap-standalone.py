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

import getopt, sys, os, os.path, commands,urllib,xml.dom.minidom, getopt, shutil, time, re, glob, pap_utils

command = None
options = {}

def build_cmd_string(args):
    
    if args is None or len(args) == 0:
        args = ""
    
    cmd = "java %s -cp %s %s %s" % (pap_utils.build_env_vars_string(),
                                    pap_utils.build_standalone_classpath(),
                                    pap_utils.pap_standalone_class,
                                    args)
    
    return cmd
    
def start():
    
    print build_cmd_string("--repo-dir ciccio")
    pass                                                    
    

def stop():
    pass

def parse_cmd_line():   
    global command, options
    
    if len(sys.argv) == 1:
        print "No command given!"
        usage()
        sys.exit(2)
    
    
    cmd_line = sys.argv[1:]
    
    try:
        
        opts,args = getopt.getopt(cmd_line,"",[]) 
        
                
        for k,v in opts:
            options[k[2:]]=v
        
        command=args[0]
        
        
    except getopt.GetoptError, m:
        print "Error parsing command line arguments!", m
        usage()
        sys.exit(1)


def usage():
    
    usage_str = """
    Usage:
    
    pap-standalone.py [options] (start|stop)
    
    Options:
    
    --conf-dir
    
        Sets where the PAP has to look for configuration.
    
    --repo-dir
        
        Sets where the PAP will store its policy repository.
        
    
    Commands:
    
        start 
            starts a standalone PAP service
        
        stop
            stops a standalone PAP service 
    """
    print usage_str


def main():
    parse_cmd_line()
    
    try:
        if command == "start":
            start()
    
        elif command == "stop":
            stop()
        else:
            print "Unknown command!"
            usage()
            sys.exit(1)
    
    except RuntimeError, detail:
        print "PAP startup error: ",detail
        sys.exit(1)

if __name__ == '__main__':
    main()