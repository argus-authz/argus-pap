import os, os.path, glob, string

pap_app_name = "glite-authz-pap"
pap_context_name  = "%s.xml" % pap_app_name
pap_war_name = "%s.war" % pap_app_name
pap_jar_name = "%s.jar" % pap_app_name 

glite_location = os.environ.get("GLITE_LOCATION", "/opt/glite")
pap_conf_dir = os.path.join(glite_location,"etc","pap")
pap_jar_dir = os.path.join(glite_location,"share", "pap","lib")

pap_dependencies = glob.glob(pap_jar_dir+"/*.jar")

pap_jar = os.path.join(glite_location,"share","java",pap_jar_name)
pap_war = os.path.join(glite_location,"share","webapps", pap_war_name)

pap_standalone_class = "org.glite.authz.pap.server.PAPServer"

pap_client_classes = os.path.join(pap_conf_dir,"logging","client")
pap_standalone_classes = os.path.join(pap_conf_dir,"logging","standalone")

env_variables = ['GLITE_LOCATION', 'GLITE_LOCATION_VAR', 'GLITE_LOCATION_LOG']


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


if __name__ == '__main__':
    
    cp = build_client_classpath()
    
    print cp