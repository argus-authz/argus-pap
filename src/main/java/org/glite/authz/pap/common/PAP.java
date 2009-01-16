package org.glite.authz.pap.common;

import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;


public class PAP  {
    public static final String localPAPId = "Local";

    public static PAP makeLocalPAP() {
        return new PAP(localPAPId, "localhost", localPAPId);
    }
    
    private String alias = "";
    private String dn = "";
    private String endpoint = "";
    private String hostname = "";
    private boolean isPublic = false;
    private String papId = "";
    private String path = "/pap/services";
    private String port = "8443";
    private String protocol = "https";
    
    public PAP() {}
    
    public PAP(PAPData papData) {
        this(papData.getAlias(), papData.getHostname(), papData.getDn(), papData.isVisibilityPublic());
    }
    
    public PAP(String alias, String hostname, String dn) {
        this(alias, hostname, dn, false);
    }
    
    public PAP(String alias, String hostname, String dn, boolean isPublic) {
        // TODO: use assert.
        
        //papId = dn.replace('/', '_').replace('@', '-');
        papId = alias;
        
        this.dn = dn;
        
        if (alias == null)
            this.alias = papId;
        else
            this.alias = alias;
        
        this.hostname = hostname;
        
        buildEndpoint();
        
        this.isPublic = isPublic;
    }

    public String getAlias() {
        return alias;
    }

    public String getDn() {
        return dn;
    }
    
    public String getEndpoint() {
        return endpoint;
    }

    public String getHostname() {
        return hostname;
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
    
    public String getPapId() {
        return papId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPapId(String papId) {
        this.papId = papId;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
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
        if (isPublic)
            visibilityString += "PUBLIC\n";
        else
            visibilityString += "PRIVATE\n";
        
        
        return aliasString + dnString + endpointString + visibilityString;
    }
    
    public String toString() {
        String visibility = "visibility=";
        
        if (isPublic)
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

}
