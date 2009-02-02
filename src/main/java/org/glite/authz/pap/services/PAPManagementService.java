package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.papmanagement.AddTrustedPAPOperation;
import org.glite.authz.pap.authz.papmanagement.GetTrustedPAPOperation;
import org.glite.authz.pap.authz.papmanagement.ListTrustedPAPsOperation;
import org.glite.authz.pap.authz.papmanagement.RefreshPolicyCacheOperation;
import org.glite.authz.pap.authz.papmanagement.RemoveTrustedPAPOperation;
import org.glite.authz.pap.authz.papmanagement.TrustedPAPExistsOperation;
import org.glite.authz.pap.authz.papmanagement.UpdateTrustedPAPOperation;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPManagementService implements PAPManagement {

    private static final Logger log = LoggerFactory.getLogger(PAPManagementService.class);

    public boolean addTrustedPAP(PAPData papData) throws RemoteException {
        log.info("addTrustedPAP();");
        
        try {
        	AddTrustedPAPOperation.instance(papData).execute();
        } catch (RuntimeException e) {
        	ServiceClassExceptionManager.logAndThrow(log, e);
        }
        
        return true;
    }

    public boolean exists(String papId) throws RemoteException {
        log.info("exists(" + papId + ");");
        
        boolean result = false;
        
        try {
        	result = TrustedPAPExistsOperation.instance(papId).execute();
        } catch (RuntimeException e) {
        	ServiceClassExceptionManager.logAndThrow(log, e);
        }
        
        return result; 
    }

    public String[] getOrder() throws RemoteException {
		String[] papOrderArray = null;
		
		try {
        	papOrderArray = PAPManager.getInstance().getTrustedPAPOrder();
        } catch (RuntimeException e) {
        	ServiceClassExceptionManager.logAndThrow(log, e);
        }
        
		return papOrderArray;
	}

    public PAPData getTrustedPAP(String papId) throws RemoteException {
        log.info("getTrustedPAP(" + papId + ");");
        return GetTrustedPAPOperation.instance(papId).execute();
    }

    public PAPData[] listTrustedPAPs() throws RemoteException {
        log.info("listTrustedPAPs();");
        return ListTrustedPAPsOperation.instance().execute();
    }

    public String ping() throws RemoteException {
        log.info("Requested ping()");
        return "PAP v0.1";
    }

    public boolean refreshCache(String papId) throws RemoteException {
        log.info("refreshCache(" + papId + ");");
        return RefreshPolicyCacheOperation.instance(papId).execute();
    }

	public boolean removeTrustedPAP(String papId) throws RemoteException {
        log.info("removeTrustedPAP(" + papId + ");");
        
        boolean result = false;
        
        try {
        	result = RemoveTrustedPAPOperation.instance(papId).execute();
        } catch (RuntimeException e) {
        	ServiceClassExceptionManager.logAndThrow(log, e);
        }
        
        return result;
    }

	

	public boolean setOrder(String[] aliasArray) throws RemoteException {
		try {
        	PAPManager.getInstance().setTrustedPAPOrder(aliasArray);
        } catch (RuntimeException e) {
        	ServiceClassExceptionManager.logAndThrow(log, e);
        }
        
		return true;
	}
	
	public boolean updateTrustedPAP(PAPData papData) throws RemoteException {
        log.info("updateTrustedPAP(" + papData.getPapId() + "," + papData + ");");
        return UpdateTrustedPAPOperation.instance(papData).execute();
    }

}
