package org.glite.authz.pap.papmanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.glite.authz.pap.authz.papmanagement.AddTrustedPAPOperation;
import org.glite.authz.pap.authz.papmanagement.GetTrustedPAPOperation;
import org.glite.authz.pap.authz.papmanagement.ListTrustedPAPsOperation;
import org.glite.authz.pap.authz.papmanagement.RefreshPolicyCacheOperation;
import org.glite.authz.pap.authz.papmanagement.RemoveTrustedPAPOperation;
import org.glite.authz.pap.authz.papmanagement.TrustedPAPExistsOperation;
import org.glite.authz.pap.authz.papmanagement.UpdateTrustedPAPOperation;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPManagementServiceImpl implements PAPManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(PAPManagementServiceImpl.class);
    
    public void addTrustedPAP(PAP pap) throws RemoteException {
        log.info( "addTrustedPAP(" + pap + ");" );
        AddTrustedPAPOperation.instance( pap ).execute();        
    }

    public boolean exists(String papId) throws RemoteException {
    	
        log.info( "exists(" + papId + ");" );
        return TrustedPAPExistsOperation.instance( papId ).execute();
	}

    public PAP getTrustedPAP(String papId) throws RemoteException {
        log.info( "getTrustedPAP(" + papId + ");" );
        return GetTrustedPAPOperation.instance( papId ).execute();
    }

    public List<PAP> listTrustedPAPs() throws RemoteException {
        log.info( "listTrustedPAPs();" );
        return ListTrustedPAPsOperation.instance().execute();
    }

    public String ping() throws RemoteException {
    	log.info("Requested ping()");
        return "PAP v0.1";
    }

    public void refreshCache(String papId) throws RemoteException {
        
        log.info( "refreshCache(" + papId + ");" );
        RefreshPolicyCacheOperation.instance( papId ).execute();
	}

	public void removeTrustedPAP(String papId) throws NotFoundException, RemoteException {
    	
	    log.info( "removeTrustedPAP(" + papId + ");" );
	    RemoveTrustedPAPOperation.instance( papId ).execute();
	    
    }

	public void updateTrustedPAP(String papId, PAP pap) throws RemoteException {
	    log.info( "updateTrustedPAP(" + papId+","+ pap + ");" );
	    UpdateTrustedPAPOperation.instance( papId, pap ).execute();
        
    }

}
