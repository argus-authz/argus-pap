package org.glite.authz.pap.authz;

import org.glite.security.util.DN;
import org.glite.security.util.DNHandler;

public class PAPAdminFactory {

    public static final String ANY_AUTHENTICATED_USER_DN = "/O=PAP/OU=Internal/CN=Any authenticated user";

    public static final X509Principal AnyAuthenticatedUserAdmin = getDn( ANY_AUTHENTICATED_USER_DN );

    public static VOMSFQAN getFQAN( String fqan ) {

        fqan = fqan.replaceAll( "\\/Role=NULL", "" );
        fqan = fqan.replaceAll( "\\/Capability=NULL", "" );
        
        return new VOMSFQAN( fqan );
    }

    public static X509Principal getDn( String dn ) {

        DN theDN = DNHandler.getDN( dn );
        
        return new X509Principal( theDN.getX500() );

    }

    public static X509Principal getAnyAuthenticatedUserAdmin() {

        return AnyAuthenticatedUserAdmin;
    }

}
