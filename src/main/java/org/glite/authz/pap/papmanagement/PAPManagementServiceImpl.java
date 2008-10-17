package org.glite.authz.pap.papmanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPManagementServiceImpl implements PAPManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(PAPManagementServiceImpl.class);
    private static final PAPManager papManager = PAPManager.getInstance();

    public void addTrustedPAP(PAP pap) throws RemoteException {
        log.info("Received request");
        papManager.add(pap);
        log.info("Added PAP: " + pap.toString());
    }

    public boolean exists(String papId) throws RemoteException {
    	log.info("Received request exists for papId=" + papId);
    	boolean result = papManager.exists(papId);
    	log.info("Sent papId=" + papId + " exists: " + result);
    	return result;
	}

    public PAP getTrustedPAP(String papId) throws RemoteException {
        PAP pap = papManager.get(papId);
        log.info("Retrieved information about PAP: " + pap.toString());
        return null;
    }

    public List<PAP> listTrustedPAPs() throws RemoteException {
        List<PAP> papList = papManager.getAll();
        log.info("Sending list of PAPs...");
        return papList;
    }

    public String ping() throws RemoteException {
    	log.info("Requested ping()");
        return "PAP v0.1";
    }

    public void refreshCache(String papId) throws RemoteException {
    	log.info("Requested refreshCache for PAP: " + papId);
    	
    	PAPManager papManager = PAPManager.getInstance();
    	
    	PAP pap;
    	
    	try {
    		pap = papManager.get(papId);
    	} catch (NotFoundException e) {
    		log.error("Unable to refresh cache, PAP not found: " + papId);
    		throw e;
    	}
    	
    	DistributionModule.refreshCache(pap);
    	
    	log.info("Cache for PAP \"" + papId + "\" has been refreshed");
	}

	public void removeTrustedPAP(String papId) throws NotFoundException, RemoteException {
    	log.info("Requested to remove pap: " + papId);
    	
    	try {
    		PAP pap = papManager.delete(papId);
    		log.info("Removed PAP: " + pap.toString());
    	} catch (NotFoundException e) {
    		throw new RemoteException(e.getMessage(), e);
    	}
    }

	public void updateTrustedPAP(String papId, PAP pap) throws RemoteException {
        papManager.update(papId, pap);
        log.info("Updated PAP: " + pap.toString());
    }

}
