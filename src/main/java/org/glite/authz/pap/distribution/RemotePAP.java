package org.glite.authz.pap.distribution;

public class RemotePAP {
	
	private String endpoint;
	private String dn;
	
	public RemotePAP(String endpoint, String dn) {
		this.endpoint = endpoint;
		this.dn = dn;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

}
