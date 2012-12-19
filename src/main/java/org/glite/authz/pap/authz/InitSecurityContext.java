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

import java.math.BigInteger;
import java.security.cert.X509Certificate;

import javax.servlet.ServletRequest;

import org.glite.authz.pap.authz.exceptions.PAPAuthzException;
import org.italiangrid.utils.voms.SecurityContext;
import org.italiangrid.utils.voms.SecurityContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.impl.X500NameUtils;

/**
 * 
 * This class is responsible for properly initializing the security context as a
 * consequence of client's request. If no valid X509 certificate chain is found
 * in the request context, a {@link PAPAuthzException} is thrown
 * 
 */
public class InitSecurityContext {

    /** The index string for the remote address as stored in the security context **/
    static public final String SECURITY_CONTEXT_REMOTE_ADDRESS = "org.glite.authz.pap.remote_address";

    static Logger logger = LoggerFactory.getLogger( InitSecurityContext.class );

    /**
     * Initializes the context from a servlet request.
     * @param request
     */
    public static void setContextFromRequest( final ServletRequest request ) {

        SecurityContextImpl sc = new SecurityContextImpl();
        SecurityContextImpl.setCurrentContext( sc );

        String remoteAddress = request.getRemoteAddr();
        sc.setRemoteAddr(remoteAddress);

        X509Certificate[] certChain = null;
        try {

            certChain = (X509Certificate[]) request
                    .getAttribute( "javax.servlet.request.X509Certificate" );

        } catch ( Exception e ) {

            logger.error( "Exception during certificate chain retrieval: " + e );
            throw new PAPAuthzException( "No certificate found in request!", e );

        }
        
        if (certChain == null)
            throw new PAPAuthzException( "No certificate found in request!"); 

        sc.setClientCertChain( certChain );

        String subject = X500NameUtils.getReadableForm(sc.getClientX500Principal());
        String issuer = X500NameUtils.getReadableForm(sc.getIssuerX500Principal());
        
        BigInteger sn = sc.getClientCert().getSerialNumber();
        String serialNumber = ( sn == null ) ? "NULL" : sn.toString();

        logger.info( "Connection from \"" + remoteAddress + "\" by \""
                + subject + "\" (issued by \"" + issuer
                + "\", " + "serial " + serialNumber + ")" );

    }

}
