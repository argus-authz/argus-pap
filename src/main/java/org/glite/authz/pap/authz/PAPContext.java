package org.glite.authz.pap.authz;

public class PAPContext {

    String name;
    ACL acl;

    private PAPContext(String name) {

	this.name = name;
	acl = new ACL();

    }

    public String getName() {

	return name;
    }

    public void setName(String name) {

	this.name = name;
    }

    public ACL getAcl() {

	return acl;
    }

    public void setAcl(ACL acl) {

	this.acl = acl;
    }

    public static PAPContext instance(String name) {

	return new PAPContext(name);
    }
}
