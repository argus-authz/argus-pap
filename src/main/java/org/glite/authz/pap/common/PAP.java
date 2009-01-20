package org.glite.authz.pap.common;

import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;


public class PAP  {
    public static final String localPAPId = "Local";
    public static String DEFAULT_SERVICES_ROOT_PATH = "/glite-authz-pap/services/";
    public static String DEFAULT_PORT = "4554";
    public static String DEFAULT_HOST = "localhost";
    public static String DEFAULT_PROTOCOL = "https";

    public static PAP makeLocalPAP() {
        return new PAP(localPAPId, localPAPId, "localhost");
    }
    
    private String alias;
    private String dn;
    private String endpoint = "";
    private String hostname = DEFAULT_HOST;
    private boolean visibilityPublic = false;
    private String papId = "";
    private String path = DEFAULT_SERVICES_ROOT_PATH;
    private String port = DEFAULT_PORT;
    private String protocol = DEFAULT_PROTOCOL;

    public PAP(PAPData papData) {
        this(papData.getAlias(), papData.getDn(), papData.getHostname(), papData.getPort(), papData.getPath(), papData
                .getProtocol(), papData.isVisibilityPublic());
    }
    
    public PAP(String alias, String dn, String hostname) {
        this(alias, dn, hostname, false);
    }
    
    public PAP(String alias, String dn, String hostname, boolean isPublic) {
        this(alias, dn, hostname, null, null, null, isPublic);
    }
    
    public PAP(String alias, String dn, String hostname, String port, String servicesRootPath, boolean visibilityPublic) {
        this(alias, dn, hostname, port, servicesRootPath, null, visibilityPublic);
    }
    
    public PAP(String alias, String dn, String hostname, String port, String servicesRootPath, String protocol, boolean visibilityPublic)
    {
        // TODO: use assert.
        
        this.alias = alias;
        this.dn = dn;
        if (isDefined(hostname))
            this.hostname = hostname;
        if (isDefined(port))
            this.port = port;
        if (isDefined(servicesRootPath))
            this.path = servicesRootPath;
        if (isDefined(protocol))
            this.protocol = protocol;
        this.visibilityPublic = visibilityPublic;
        
        this.papId = alias;
        
        buildEndpoint();
    }
    
    public String getAlias() {
        return alias;
    }

    public String getDn() {
        return dn;
    }
    
    public String getEndpoint() {
        buildEndpoint();
        return endpoint;
    }

    public String getHostname() {
        return hostname;
    }
    
    public String getPapId() {
        return papId;
    }
    
    public String getPath() {
        return path;
    }

    public String getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isVisibilityPublic() {
        return visibilityPublic;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public void setDn(String dn) {
        this.dn = dn;
    }
    
//    public void setEndpoint(String endpoint) {
//        this.endpoint = endpoint;
//    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPapId(String papId) {
        this.papId = papId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setPublic(boolean isPublic) {
        this.visibilityPublic = isPublic;
    }
    
    public void setVisibilityPublic(boolean visibilityPublic) {
        this.visibilityPublic = visibilityPublic;
    }

    public String toFormattedString() {
    	return toFormattedString(0, 4);
    }

    public String toFormattedString(int indent) {
    	return toFormattedString(indent, 4);
    }
    
    public String toFormattedString(int indent, int padding) {
    	
    	String indentString = fillWithSpaces(indent);
    	String paddingString = fillWithSpaces(indent + padding);
    	
    	String aliasString = indentString + "alias=\"" + alias + "\"\n";
    	String dnString = paddingString + "dn=\"" + dn + "\"\n";
    	String endpointString = paddingString + "endpoint=\"" + endpoint + "\"\n";
        String visibilityString = paddingString + "visibility=";
        if (visibilityPublic)
            visibilityString += "PUBLIC\n";
        else
            visibilityString += "PRIVATE\n";
        
        
        return aliasString + dnString + endpointString + visibilityString;
    }
    
    public String toString() {
        String visibility = "visibility=";
        
        if (visibilityPublic)
            visibility += "PUBLIC";
        else
            visibility += "PRIVATE";
        
        return "alias=\"" + alias + "\" dn=\"" + dn + "\" endpoint=\"" + endpoint + "\" id=\"" + papId + "\"";
    }
    
    private void buildEndpoint() {
    
        endpoint = protocol + "://" + hostname + ":" + port + path;
        
    }
    
    private String fillWithSpaces(int n) {
    	String s = "";
    	
    	for (int i=0; i<n; i++) {
    		s += " ";
    	}
    	
    	return s;
    }
    
    private boolean isDefined(String s) {
        
        if (s == null)
            return false;
        
        if (s.length() == 0)
            return false;
        
        return true;
        
    }

}
