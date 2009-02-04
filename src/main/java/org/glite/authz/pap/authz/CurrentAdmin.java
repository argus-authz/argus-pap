package org.glite.authz.pap.authz;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.glite.authz.pap.common.utils.PathNamingScheme;
import org.glite.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glite.voms.VOMSValidator;

public class CurrentAdmin {

    static Logger log = LoggerFactory.getLogger( CurrentAdmin.class );

    private PAPAdmin papAdmin;

    private VOMSFQAN[] fqans;

    protected CurrentAdmin( PAPAdmin admin, VOMSFQAN[] fqans ) {

        this.papAdmin = admin;
        this.fqans = fqans;
    }

    public static CurrentAdmin instance() {

        SecurityContext theContext = SecurityContext.getCurrentContext();

        String adminDN = theContext.getClientName();

        X509Principal papAdmin = PAPAdminFactory.getDn( adminDN );

        if ( papAdmin == null )
            papAdmin = PAPAdminFactory.getAnyAuthenticatedUserAdmin();

        // Get VOMS attributes here
        VOMSValidator validator = new VOMSValidator( theContext
                .getClientCertChain() );
        try {

            validator.validate();
            String[] attrs = validator.getAllFullyQualifiedAttributes();

            if ( attrs.length > 0 ) {

                VOMSFQAN[] fqans = new VOMSFQAN[attrs.length];

                for ( int i = 0; i < attrs.length; i++ )
                    fqans[i] = PAPAdminFactory.getFQAN( attrs[i] );

                if ( log.isDebugEnabled() ) {
                    String fqansString = StringUtils.join( fqans, "," );
                    log.debug( String.format(
                            "X509Principal: '%s' has valid fqans: '%s'",
                            papAdmin, fqansString ) );
                }

                return new CurrentAdmin( papAdmin, fqans );
            } else
                log.debug( "No VOMS AC found in client certificate chain" );

        } catch ( Throwable t ) {

            log.warn(
                    "Error validating voms attributes out of the cert chain:",
                    t.getMessage() );
        }

        return new CurrentAdmin( papAdmin, null );
    }

    public boolean hasPermissions( PAPContext context,
            PAPPermission requiredPerms ) {

        PAPPermissionList currentAdminPermList = PAPPermissionList.instance();

        PAPPermission adminPerms = context.getAcl().getPermissions().get(
                papAdmin );
        PAPPermission anyUserPerms = context.getAcl()
                .getAnyAuthenticatedUserPermissions();

        currentAdminPermList.addPermission( adminPerms );
        currentAdminPermList.addPermission( anyUserPerms );

        if ( fqans != null ) {

            for ( VOMSFQAN fqan : fqans ) {

                PAPPermission fqanPerms = context.getAcl().getPermissions()
                        .get( fqan );

                if ( fqanPerms != null ) {
                    currentAdminPermList.addPermission( fqanPerms );
                    log.debug( "Adding permissions '{}' for fqan '{}'",
                            fqanPerms, fqan );
                }

            }

        }

        if ( log.isDebugEnabled() ) {
            log.debug( String.format(
                    "Checking perms '%s' for admin '%s' in context '%s'",
                    requiredPerms, papAdmin, context ) );
            log.debug( String.format( "CurrrentAdminPerms: '%s'",
                    currentAdminPermList ) );
        }

        return currentAdminPermList.satisfies( requiredPerms );

    }

}
