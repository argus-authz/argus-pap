package org.glite.authz.pap.common;

public class PAP {
    public static final String localPAPId = "Local";

    private String papId;
    private String endpoint;
    private String dn;

    public PAP(String endpoint, String dn) {
	this.papId = dn;
	this.endpoint = endpoint;
	this.dn = dn;
    }

    public PAP(String papId) {
	this(null, papId);
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

    public void setDn(String dn) {
	this.dn = dn;
    }

    public void setEndpoint(String endpoint) {
	this.endpoint = endpoint;
    }

    public void setPapId(String papId) {
	this.papId = papId;
    }

}
