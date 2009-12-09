package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.operations.papmanagement.AddPapOperation;
import org.glite.authz.pap.authz.operations.papmanagement.GetOrderOperation;
import org.glite.authz.pap.authz.operations.papmanagement.GetPapOperation;
import org.glite.authz.pap.authz.operations.papmanagement.GetPollingIntervalOperation;
import org.glite.authz.pap.authz.operations.papmanagement.ListPapsOperation;
import org.glite.authz.pap.authz.operations.papmanagement.PapExistsOperation;
import org.glite.authz.pap.authz.operations.papmanagement.RefreshPolicyCacheOperation;
import org.glite.authz.pap.authz.operations.papmanagement.RemovePapOperation;
import org.glite.authz.pap.authz.operations.papmanagement.SetEnabledOperation;
import org.glite.authz.pap.authz.operations.papmanagement.SetOrderOperation;
import org.glite.authz.pap.authz.operations.papmanagement.SetPollingIntervalOperation;
import org.glite.authz.pap.authz.operations.papmanagement.UpdatePapOperation;
import org.glite.authz.pap.common.PAPVersion;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManagerException;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPManagementService implements PAPManagement {

    private static final Logger log = LoggerFactory.getLogger(PAPManagementService.class);

    public boolean addPap(Pap pap) throws RemoteException {
        log.info("addTrustedPAP();");

        try {
            
            ServicesUtils.beginTransaction();
            
            boolean result = AddPapOperation.instance(pap).execute();
            
            ServicesUtils.beginTransaction();
            
            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean exists(String papAlias) throws RemoteException {
        log.info("exists(" + papAlias + ");");

        try {
            ServicesUtils.beginTransaction();
            
            boolean result = PapExistsOperation.instance(papAlias).execute();
            
            ServicesUtils.beginTransaction();
            
            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }

    }

    public Pap[] getAllPaps() throws RemoteException {
        log.info("listTrustedPAPs();");
        try {

            ServicesUtils.beginTransaction();
            
            Pap[] papArray = ListPapsOperation.instance().execute();
            
            ServicesUtils.beginTransaction();
            
            return papArray;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public String[] getOrder() throws RemoteException {
        try {

            ServicesUtils.beginTransaction();
            
            String[] result = GetOrderOperation.instance().execute();
            
            ServicesUtils.beginTransaction();
            
            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public Pap getPap(String papAlias) throws RemoteException {
        log.info("getTrustedPAP(" + papAlias + ");");
        try {

            ServicesUtils.beginTransaction();
            
            Pap result = GetPapOperation.instance(papAlias).execute();
            
            ServicesUtils.beginTransaction();
            
            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public float getPollingInterval() throws RemoteException {
        log.info("getPollingInterval();");
        try {

            ServicesUtils.beginTransaction();
            
            float result = GetPollingIntervalOperation.instance().execute();
            
            ServicesUtils.beginTransaction();
            
            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public String ping() throws RemoteException {
        log.info("Requested ping()");
        return "PAP version: " + PAPVersion.instance().getVersion();
    }

    public boolean refreshCache(String papAlias) throws RemoteException {
        log.info("refreshCache(" + papAlias + ");");
        try {

            ServicesUtils.beginTransaction();
            
            boolean result = RefreshPolicyCacheOperation.instance(papAlias).execute();
            
            ServicesUtils.beginTransaction();
            
            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean removePap(String papAlias) throws RemoteException {
        log.info("removeTrustedPAP(" + papAlias + ");");
        try {

            ServicesUtils.beginTransaction();
            
            boolean result = RemovePapOperation.instance(papAlias).execute();
            
            ServicesUtils.beginTransaction();
            
            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public void setEnabled(String alias, boolean enabled) throws RemoteException {
        log.info("setEnabled(" + alias + ", " + enabled + ");");
        try {

            ServicesUtils.beginTransaction();
            
            SetEnabledOperation.instance(alias, enabled).execute();
            
            ServicesUtils.beginTransaction();

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean setOrder(String[] aliasArray) throws RemoteException {
        try {

            ServicesUtils.beginTransaction();
            
            boolean result = SetOrderOperation.instance(aliasArray).execute();
            
            ServicesUtils.beginTransaction();
            
            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public void setPollingInterval(float seconds) throws RemoteException {
        log.info("setPollingInterval(" + seconds + ");");
        try {

            SetPollingIntervalOperation.instance((long) seconds).execute();

        } catch (RuntimeException e) {
            ServicesExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean updatePap(Pap pap) throws RemoteException {
        log.info("updateTrustedPAP(" + pap.getAlias() + "," + pap + ");");
        try {

            if (pap.isDefaultPap()) {
                throw new PapManagerException(String.format("Invalid request. \"%s\" cannot be updated",
                                                            Pap.DEFAULT_PAP_ALIAS));
            }

            ServicesUtils.beginTransaction();
            
            boolean result = UpdatePapOperation.instance(pap).execute();
            
            ServicesUtils.beginTransaction();
            
            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }
}
