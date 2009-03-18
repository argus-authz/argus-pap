package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.papmanagement.AddTrustedPAPOperation;
import org.glite.authz.pap.authz.papmanagement.GetOrderOperation;
import org.glite.authz.pap.authz.papmanagement.GetTrustedPAPOperation;
import org.glite.authz.pap.authz.papmanagement.ListTrustedPAPsOperation;
import org.glite.authz.pap.authz.papmanagement.RefreshPolicyCacheOperation;
import org.glite.authz.pap.authz.papmanagement.RemoveTrustedPAPOperation;
import org.glite.authz.pap.authz.papmanagement.SetOrderOperation;
import org.glite.authz.pap.authz.papmanagement.TrustedPAPExistsOperation;
import org.glite.authz.pap.authz.papmanagement.UpdateTrustedPAPOperation;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManagerException;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPManagementService implements PAPManagement {

    private static final Logger log = LoggerFactory.getLogger(PAPManagementService.class);

    public boolean addPAP(PAPData papData) throws RemoteException {
        log.info("addTrustedPAP();");

        try {

            return AddTrustedPAPOperation.instance(papData).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean exists(String papAlias) throws RemoteException {
        log.info("exists(" + papAlias + ");");

        try {

            return TrustedPAPExistsOperation.instance(papAlias).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }

    }

    public PAPData[] getAllPAPs() throws RemoteException {
        log.info("listTrustedPAPs();");
        try {

            return ListTrustedPAPsOperation.instance().execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String[] getOrder() throws RemoteException {
        try {

            return GetOrderOperation.instance().execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PAPData getPAP(String papAlias) throws RemoteException {
        log.info("getTrustedPAP(" + papAlias + ");");
        try {

            return GetTrustedPAPOperation.instance(papAlias).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String ping() throws RemoteException {
        log.info("Requested ping()");
        return "PAP v0.9.1";
    }

    public boolean refreshCache(String papAlias) throws RemoteException {
        log.info("refreshCache(" + papAlias + ");");
        try {

            return RefreshPolicyCacheOperation.instance(papAlias).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removePAP(String papAlias) throws RemoteException {
        log.info("removeTrustedPAP(" + papAlias + ");");
        try {

            return RemoveTrustedPAPOperation.instance(papAlias).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean setOrder(String[] aliasArray) throws RemoteException {
        try {

            return SetOrderOperation.instance(aliasArray).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean updatePAP(PAPData papData) throws RemoteException {
        log.info("updateTrustedPAP(" + papData.getAlias() + "," + papData + ");");
        try {

            if (PAP.DEFAULT_PAP_ALIAS.equals(papData.getAlias())) {
                throw new PAPManagerException(String.format("Invalid request. \"%s\" cannot be updated", PAP.DEFAULT_PAP_ALIAS));
            }

            return UpdateTrustedPAPOperation.instance(papData).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

}
