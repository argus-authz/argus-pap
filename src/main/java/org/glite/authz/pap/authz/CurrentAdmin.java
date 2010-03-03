/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.authz;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.glite.authz.pap.authz.exceptions.PAPAuthzException;
import org.glite.authz.pap.common.exceptions.NullArgumentException;
import org.glite.security.SecurityContext;
import org.glite.voms.VOMSAttribute;
import org.glite.voms.VOMSValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class describes the currently authenticated PAP administrator.
 * 
 */
public class CurrentAdmin {

    static Logger log = LoggerFactory.getLogger( CurrentAdmin.class );

    /**
     * The static validator object used to check VOMS attribute certificate
     * validity
     **/
    static VOMSValidator validator = null;

    /** The PAPAdmin object representing the current administrator **/
    private PAPAdmin papAdmin;

    /** The array of signed fqans the current administators has **/
    private VOMSFQAN[] fqans;

    /**
     * Constructor
     * 
     * @param admin
     *            , the PAPAdmin object referring to the current administrator
     * 
     */
    protected CurrentAdmin( PAPAdmin admin ) {

        this.papAdmin = admin;
    }

    /**
     * This method looks for trusted VOMS FQANs in the security context. The found
     * fqans are stored in the {@link #fqans} array.
     */
    protected void getFQANsFromSecurityContext() {

        log.debug( "Fectching FQANs out of the security context");
        
        SecurityContext theContext = SecurityContext.getCurrentContext();
        
        if ( validator == null ) {
            log.debug("Initializing VOMS validator object...");
            validator = new VOMSValidator(theContext.getClientCertChain());
            
        }else
            validator.setClientChain( theContext.getClientCertChain() );
        
        try {

            validator.validate();

            List <VOMSAttribute> attrs = validator.getVOMSAttributes();

            List <VOMSFQAN> myFQANs = new ArrayList <VOMSFQAN>();

            for ( VOMSAttribute voAttr : attrs ) {

                List <String> fqanAttrs = voAttr.getFullyQualifiedAttributes();

                if ( fqanAttrs.size() > 0 ) {

                    for ( String f : fqanAttrs )
                        myFQANs.add( PAPAdminFactory.getFQAN( f ) );

                    if ( log.isDebugEnabled() ) {

                        String fqansString = StringUtils.join( myFQANs, "," );
                        log.debug( "X509Principal: '{}' has valid fqans: '{}'",
                                papAdmin, fqansString );
                    }

                }

            }

            if ( myFQANs.size() > 0 )
                fqans = (VOMSFQAN[]) myFQANs.toArray( new VOMSFQAN[0] );
            else
                log.debug( "No VOMS AC found in client certificate chain" );

        } catch ( Throwable t ) {

            log.warn(
                    "Error validating voms attributes out of the cert chain:",
                    t.getMessage() );
        }

    }

    /**
     * Returns the currently active administrator as taken from the
     * {@link SecurityContext}
     * 
     * @return the {@link CurrentAdmin} object representing the currently active
     *         administrator
     */
    public static CurrentAdmin instance() {

        SecurityContext theContext = SecurityContext.getCurrentContext();

        String adminDN = theContext.getClientName();

        X509Principal papAdmin = PAPAdminFactory.getDn( adminDN );

        if ( papAdmin == null )
            papAdmin = PAPAdminFactory.getAnyAuthenticatedUserAdmin();

        return new CurrentAdmin( papAdmin );
    }

    /**
     * Checks if a context has permissions defined in its ACL for FQAN 
     * administrators
     * 
     * @param context the context to check
     * @return <code>true</code> if one FQAN admin is found, <code>false</code> otherwise
     */
    private boolean contextHasFQANPermissions(PAPContext context){
        
        if ( context == null )
            throw new NullArgumentException(
                    "Please provide a value for the 'context' argument! null is not a valid value in this context." );
        
        ACL acl = context.getAcl();
        
        if (acl == null)
            throw new PAPAuthzException("FATAL ERROR: No ACL defined for context '"+context.getName()+"'!");
        
        for(PAPAdmin a: acl.getPermissions().keySet())
            if (a instanceof VOMSFQAN)
                return true;
        
        return false;
        
    }
    
    /**
     * Checks whether the currently active administrator has some permissions in
     * a context.
     * 
     * @param context
     *            , the context
     * @param requiredPerms
     *            , the required permissions
     */
    public boolean hasPermissions( PAPContext context,
            PAPPermission requiredPerms ) {

        PAPPermissionList currentAdminPermList = PAPPermissionList.instance();

        PAPPermission adminPerms = context.getAcl().getPermissions().get(
                papAdmin );
        PAPPermission anyUserPerms = context.getAcl()
                .getAnyAuthenticatedUserPermissions();

        currentAdminPermList.addPermission( adminPerms );
        currentAdminPermList.addPermission( anyUserPerms );

        if (contextHasFQANPermissions( context )){
            
            log.debug( "FQAN permissions defined for context '"+context+"'" );
            getFQANsFromSecurityContext();
            
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
