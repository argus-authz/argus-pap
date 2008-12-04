import os, os.path, glob

glite_location = os.environ.get("GLITE_LOCATION", "/opt/glite")
pap_conf_dir = os.path.join(glite_location,"etc","pap")
pap_jar_dir = os.path.join(pap_conf_dir,"lib")

pap_dependencies = glob.glob(pap_jar_dir+"/*.jar")
pap_jar = os.path.join(glite_location,"share","webapps","glite-authz-pap.jar")

pap_client_classes = os.path.join(pap_conf_dir,"logging","client")
pap_standalone_classes = os.path.join(pap_conf_dir,"logging","standalone")

def build_client_classpath():
    
    if len(pap_dependencies) == 0:
        raise RuntimeError, "PAP jar files not found at usual location: %" % (pap_jar_dir)
    
    
    pass
