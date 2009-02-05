package org.glite.authz.pap.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.glite.authz.pap.common.exceptions.PAPCertificateException;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;


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
    private static CertificateFactory certificateFactory;
    
    
    
    private CertificateChecker() {

        // TODO Auto-generated constructor stub
    }
    
    
    public static CertificateChecker instance() {

        return new CertificateChecker();
    }
    
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
