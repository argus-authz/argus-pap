package org.glite.authz.pap.common;

public class PAP {
    public static final String localPAPId = "Local";

    public static PAP makeLocalPAP() {
        return new PAP("local_pap", "localhost", localPAPId);
    }
    
    private String papId;
    private String endpoint;
    private String dn;
    private String alias;
    private boolean isPublic;
    
    public PAP() {
        papId = "";
        endpoint = "";
        dn = "";
        alias = "";
    }

    public PAP(String alias, String endpoint, String dn) {
        this(alias, endpoint, dn, true);
    }

    public PAP(String alias, String endpoint, String dn, boolean isPublic) {
        papId = dn.replace('/', '_').replace('@', '-');
        
        this.dn = dn;
        
        if (alias == null)
            this.alias = papId;
        else
            this.alias = alias;
        
        if (endpoint == null)
            this.endpoint = "NULL";
        else
            this.endpoint = endpoint;
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

    public void setPapId(String papId) {
        this.papId = papId;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String toString() {
        String visibility = "visibility=";
        
        if (isPublic)
            visibility += "PUBLIC";
        else
            visibility += "PRIVATE";
        
        return "alias=\"" + alias + "\" dn=\"" + dn + "\" endpoint=\"" + endpoint + "\" id=\"" + papId + "\"";
    }

}
