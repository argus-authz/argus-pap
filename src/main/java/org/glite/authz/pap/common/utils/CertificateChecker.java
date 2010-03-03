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

package org.glite.authz.pap.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.glite.authz.pap.common.exceptions.PAPCertificateException;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;

/**
 * Performs some sanity checks on X509 certificates
 * 
 * 
 *
 */
// TODO: Still need to implement all the checks here.
public class CertificateChecker {
    
    static{
        
        try {
            
            if (Security.getProvider( "BC" ) == null){
                Security.addProvider( new BouncyCastleProvider() );
            }
            
            certificateFactory = CertificateFactory.getInstance( "X.509","BC" );
        
        } catch ( Exception e ) {
            throw new PAPConfigurationException("Error instantiating x509 certificate factory! Check that your bouncycastle jars are in place!");
        }
    }
  
    /**
     * The certificate factory used to parse and create X509 certificates.
     */
    private static CertificateFactory certificateFactory;
    
    
    /**
     * Constructor
     */
    private CertificateChecker() {

        // TODO Auto-generated constructor stub
    }
    
    /**
     *  
     * @return a new instance of the {@link CertificateChecker}
     */
    public static CertificateChecker instance() {

        return new CertificateChecker();
    }
    
    /**
     * Performs some sanity checks on an x509 certificate
     * 
     * @param pathToCert, the path that leads to the certificate file 
     */
    public void checkCertificate(String pathToCert){
        
        File certFile = new File(pathToCert);
        
        if (!certFile.exists())
            throw new PAPCertificateException("Certificate file '"+pathToCert+"' does not exist!");
        
        if (!certFile.canRead())
            throw new PAPCertificateException("Certificate file '"+pathToCert+"' is not readable!");
        
        X509Certificate serviceCert = null; 
        
        try {
            FileInputStream fis = new FileInputStream(certFile);
            
            serviceCert = (X509Certificate) certificateFactory.generateCertificate( fis );
            
            if (serviceCert == null)
                throw new PAPCertificateException("Certificate could not be generated!");
            
            serviceCert.checkValidity();
        
        } catch ( FileNotFoundException e ) {
            
            throw new PAPCertificateException("Certificate file '"+pathToCert+"' does not exist!");
            
        } catch ( CertificateExpiredException e ) {
            throw new PAPCertificateException("Certificate '"+pathToCert+"' has expired!",e);
            
        } catch ( CertificateNotYetValidException e ) {
            throw new PAPCertificateException("Certificate '"+pathToCert+"' isn't yet valid!",e);
            
        } catch ( CertificateException e ) {
            throw new PAPCertificateException("Error parsing certificate file '"+pathToCert+"': "+ e.getMessage(),e);
        }  
        
    }
    
    /**
     * Performs some sanity checks on an x509 private key 
     * 
     * @param pathToKey, the path that leads to the private key file
     */
    public void checkPrivateKey(String pathToKey){
        
        File keyFile = new File(pathToKey);
        if (!keyFile.exists())
            throw new PAPCertificateException("Private key file '"+pathToKey+"' doesn't exist!");
    }
    
    
    public static void main( String[] args ) {

        CertificateChecker cc = CertificateChecker.instance();
        
        cc.checkCertificate( "/etc/grid-security/hostcert.pem" );
        cc.checkPrivateKey( "/etc/grid-security/hostkey.pem" );
        
        
    }

}
