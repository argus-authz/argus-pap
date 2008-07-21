package org.glite.authz.pap.authz;

import org.glite.security.SecurityContext;
import org.slf4j.Logger;

public class CurrentAdmin {

    Logger log;

    private PAPAdmin papAdmin;

    protected CurrentAdmin(PAPAdmin admin) {

	this.papAdmin = admin;
    }

    public static CurrentAdmin instance() {

	SecurityContext theContext = SecurityContext.getCurrentContext();

	String adminDN = theContext.getClientName();

	X509Principal papAdmin = PAPAdminFactory.getDn(adminDN);

	if (papAdmin == null)
	    papAdmin = PAPAdminFactory.getAnyAuthenticatedUserAdmin();

	return new CurrentAdmin(papAdmin);
    }

    public boolean hasPermissions(PAPContext context,
	    PAPPermission requiredPerms) {

	PAPPermissionList currentAdminPermList = PAPPermissionList.instance();

	PAPPermission adminPerms = context.getAcl().getPermissions().get(
		papAdmin);
	PAPPermission anyUserPerms = context.getAcl()
		.getAnyAuthenticatedUserPermissions();

	currentAdminPermList.addPermission(adminPerms);
	currentAdminPermList.addPermission(anyUserPerms);

	return currentAdminPermList.satisfies(requiredPerms);

    }

}
