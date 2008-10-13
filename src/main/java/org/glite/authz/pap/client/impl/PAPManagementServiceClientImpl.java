package org.glite.authz.pap.client.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.PAPManagementService;

public class PAPManagementServiceClientImpl implements PAPManagementService {
    
    private static final QName PAP_QNAME;
    private static final BeanSerializerFactory serializerFactory;
    private static final BeanDeserializerFactory deserializerFactory;
    static {
        PAP_QNAME = new QName("http://common.pap.authz.glite.org", "PAP");
        serializerFactory = new BeanSerializerFactory(PAP.class, PAP_QNAME);
        deserializerFactory = new BeanDeserializerFactory(PAP.class, PAP_QNAME);
    }
    private final Service service;
    
    private final String serviceURL;
    
    public PAPManagementServiceClientImpl(String serviceURL, Service service) {
        this.service = service;
        this.serviceURL = serviceURL;
    }

    public void addTrustedPAP(PAP pap) throws RemoteException {
        Call call = createCall("addTrustedPAP");
        call.registerTypeMapping(PAP.class, PAP_QNAME , serializerFactory, deserializerFactory);
        
        call.invoke(new Object[] { pap } );
    }

    private Call createCall(String operationName) {
        Call call;
        try {
            call = (Call) service.createCall();
        } catch (ServiceException e) {
            throw new RuntimeException("Error", e);
        }

        call.setTargetEndpointAddress(serviceURL);
        call.setOperationName(new QName("urn:org:glite:authz:pap:papmanagement", operationName));

        call.setOperationStyle(Style.RPC);
        call.setOperationUse(Use.LITERAL);
        
        return call;
    }

    public boolean exists(String papId) throws RemoteException {
    	Call call = createCall("exists");
		String dirtyTrick = (String) call.invoke(new Object[] { papId });
		return Boolean.parseBoolean(dirtyTrick);
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
        
        Object object = call.invoke(new Object[] { } );
        
        List<PAP> papList = new ArrayList<PAP>(0);
        
        if (object == null)
        	return papList;
        
        if (object instanceof PAP)
        	papList.add((PAP) object);
        else
        	papList = (List<PAP>) object;
        
        return papList;
    }

    public String ping() throws RemoteException {
        Call call = createCall("ping");
        return (String) call.invoke(new Object[] { } );
    }

    public void removeTrustedPAP(String papId) throws RemoteException {
        Call call = createCall("removeTrustedPAP");
        call.invoke(new Object[] { papId } );
    }

    public void updateTrustedPAP(PAP pap) throws RemoteException {
        
    }

	public void updateTrustedPAP(String papId, PAP newpap) throws RemoteException {
        Call call = createCall("updateTrustedPAP");
        call.registerTypeMapping(PAP.class, PAP_QNAME , serializerFactory, deserializerFactory);
        call.invoke(new Object[] { papId, newpap } );
    }

}
