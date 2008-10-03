package org.glite.authz.pap.papmanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPManagementServiceImpl implements PAPManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(PAPManagementServiceImpl.class);
    private static final PAPManager papManager = PAPManager.getInstance();

    public void addTrustedPAP(PAP pap) throws RemoteException {
        papManager.add(pap);
        log.info("Added PAP: " + pap.toString());
    }

    public PAP getTrustedPAP(String papId) throws RemoteException {
        PAP pap = papManager.get(papId);
        log.info("Retrieved information about PAP: " + pap.toString());
        return null;
    }

    public List<PAP> listTrustedPAPs() throws RemoteException {
        List<PAP> papList = papManager.getAll();
        log.info("Sent list of PAPs");
        return papList;
    }

    public String ping() throws RemoteException {
        return "PAP v0.1";
    }

    public void removeTrustedPAP(String papId) throws RemoteException {
        PAP pap = papManager.delete(papId);
        log.info("Removed PAP: " + pap.toString());
    }

    public void updateTrustedPAP(String papId, PAP pap) throws RemoteException {
        papManager.update(papId, pap);
        log.info("Updated PAP: " + pap.toString());
    }

}
