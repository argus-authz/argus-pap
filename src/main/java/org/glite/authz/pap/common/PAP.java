package org.glite.authz.pap.common;

import java.util.Date;

import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;


public class PAP  {
    public static String DEFAULT_DN = "invalid_dn";
    public static String DEFAULT_HOST = "localhost";
    public static String DEFAULT_PORT = "4554";
    public static String DEFAULT_PROTOCOL = "https";
    public static String DEFAULT_SERVICES_ROOT_PATH = "/glite-authz-pap/services/";
    public static final String localPAPAlias = "Local";

    private String alias;
    private String dn;
    private String hostname;
    private String papId;
    private String path;
    private Date policyLastModificationTime = null;
    private String port;
    private String protocol;
    private boolean visibilityPublic;
    
    public PAP(PAPData papData) {
        this(papData.getAlias(), papData.getDn(), papData.getHostname(), papData.getPort(), papData.getPath(), papData
                .getProtocol(), papData.isVisibilityPublic());
    }

    public PAP(String alias) {
    	this(alias, null, null, null, null, null, false);
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
        // TODO: assert alias != null and empty string
        
        this.alias = alias;
        this.papId = alias;
        this.visibilityPublic = visibilityPublic;
        
        this.dn = DEFAULT_DN;
        this.hostname = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
        this.path = DEFAULT_SERVICES_ROOT_PATH;
        this.protocol = DEFAULT_PROTOCOL;
        
        if (isDefined(dn))
        	this.dn = dn;
        if (isDefined(hostname))
            this.hostname = hostname;
        if (isDefined(port))
            this.port = port;
        if (isDefined(servicesRootPath))
            this.path = servicesRootPath;
        if (isDefined(protocol))
            this.protocol = protocol;
    }
    
    public static PAP makeLocalPAP() {
        return new PAP(localPAPAlias, localPAPAlias, "localhost", true);
    }
    
    public boolean equals(PAP pap) {
    	
    	if (pap == null)
    		return false;
    	if (!alias.equals(pap.getAlias()))
    		return false;
    	if (!dn.equals(pap.getDn()))
    		return false;
    	if (!hostname.equals(pap.getHostname()))
    		return false;
    	if (!port.equals(pap.getPort()))
    		return false;
    	if (!path.equals(pap.getPath()))
    		return false;
    	if (!protocol.equals(pap.getProtocol()))
    		return false;
    	if (!(visibilityPublic == pap.isVisibilityPublic()))
    		return false;
    	
    	return true;
	}
    
    public String getAlias() {
        return alias;
    }
    
	public String getDn() {
        return dn;
    }

	public String getEndpoint() {
        return protocol + "://" + hostname + ":" + port + path;
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
    
    public Date getPolicyLastModificationTime() {
        return policyLastModificationTime;
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
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    public void setPapId(String papId) {
        this.papId = papId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPolicyLastModificationTime(Date policyLastModificationTime) {
        this.policyLastModificationTime = policyLastModificationTime;
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
    	String endpointString = paddingString + "endpoint=\"" + getEndpoint() + "\"\n";
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
        
        return "alias=\"" + alias + "\" dn=\"" + dn + "\" endpoint=\"" + getEndpoint() + "\" id=\"" + papId + "\"";
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
