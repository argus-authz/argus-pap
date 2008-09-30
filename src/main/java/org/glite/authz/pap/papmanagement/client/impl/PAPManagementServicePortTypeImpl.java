package org.glite.authz.pap.papmanagement.client.impl;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisProperties;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.client.PAPManagementServicePortType;
import org.glite.security.trustmanager.axis.AXISSocketFactory;

public class PAPManagementServicePortTypeImpl implements PAPManagementServicePortType {
    
    private static final QName PAP_QNAME;
    private static final BeanSerializerFactory serializerFactory;
    private static final BeanDeserializerFactory deserializerFactory;
    private String url;
    private String clientCertificate;
    private String clientPrivateKey;
    private String clientPrivateKeyPassword;
    private Service service;
    
    static {
        PAP_QNAME = new QName("http://common.pap.authz.glite.org", "PAP");
        serializerFactory = new BeanSerializerFactory(PAP.class, PAP_QNAME);
        deserializerFactory = new BeanDeserializerFactory(PAP.class, PAP_QNAME);
    }
    
    public PAPManagementServicePortTypeImpl(String url) {
        this.url = url;
        init();
    }

    public void addTrustedPAP(PAP pap) throws RemoteException {
        Call call = createCall("addTrustedPAP");
        call.registerTypeMapping(PAP.class, PAP_QNAME , serializerFactory, deserializerFactory);
        
        call.invoke(new Object[] { pap } );
    }

    public PAP getTrustedPAP(String papId) throws RemoteException {
        Call call = createCall("getTrustedPAP");
        call.registerTypeMapping(PAP.class, PAP_QNAME , serializerFactory, deserializerFactory);
        
        return (PAP) call.invoke(new Object[] { papId } );
    }

    @SuppressWarnings("unchecked")
    public List<PAP> listTrustedPAPs() throws RemoteException {
        Call call = createCall("listTrustedPAP");
        call.registerTypeMapping(PAP.class, PAP_QNAME , serializerFactory, deserializerFactory);
        return (List<PAP>) call.invoke(new Object[] { } );
    }

    public String ping() throws RemoteException {
        Call call = createCall("ping");
        System.out.println("Doing ping...");
        return (String) call.invoke(new Object[] { } );
    }

    public void removeTrustedPAP(String papId) throws RemoteException {
        Call call = createCall("removeTrustedPAP");
        call.invoke(new Object[] { papId } );
    }

    public void setClientCertificate(String certFile) {
    // TODO Auto-generated method stub

    }
    
    public void setClientPrivateKey(String keyFile) {
    // TODO Auto-generated method stub

    }

    public void setClientPrivateKeyPassword(String privateKeyPassword) {
    // TODO Auto-generated method stub

    }

    public void setTargetEndpoint(String endpointURL) {
    // TODO Auto-generated method stub

    }

    public void updateTrustedPAP(PAP pap) throws RemoteException {
        
    }

    public void updateTrustedPAP(String papId, PAP newpap) throws RemoteException {
        Call call = createCall("updateTrustedPAP");
        call.registerTypeMapping(PAP.class, PAP_QNAME , serializerFactory, deserializerFactory);
        call.invoke(new Object[] { papId, newpap } );
    }

    private Call createCall(String operationName) {
        Call call;
        try {
            call = (Call) service.createCall();
        } catch (ServiceException e) {
            throw new RuntimeException("Error", e);
        }

        call.setTargetEndpointAddress(url);
        call.setOperationName(new QName("urn:org:glite:authz:pap:papmanagement", operationName));

        call.setOperationStyle(Style.RPC);
        call.setOperationUse(Use.LITERAL);
        
        return call;
    }

    private void init() {
        AxisProperties.setProperty("axis.socketSecureFactory",
        "org.glite.security.trustmanager.axis.AXISSocketFactory");

        // need to pass property to AXISSocketFactory
        Properties properties = AXISSocketFactory.getCurrentProperties();
        
        // TODO will get cert and key form the configuration, with those as
        // default
        
        if (clientCertificate == null)
            properties.setProperty("sslCertFile", "/etc/grid-security/hostcert.pem");
        else
            properties.setProperty("sslCertFile", clientCertificate);
        
        if (clientPrivateKey == null)
            properties.setProperty("sslKey", "/etc/grid-security/hostkey.pem");
        else
            properties.setProperty("sslKey", clientPrivateKey);
        
        if (clientPrivateKeyPassword != null)
            properties.setProperty("sslKeyPasswd", clientPrivateKeyPassword);
        
        AXISSocketFactory.setCurrentProperties(properties);
        
        service = new Service();
    }

}
