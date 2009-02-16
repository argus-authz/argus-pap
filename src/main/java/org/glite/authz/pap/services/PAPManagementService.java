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

	public boolean addTrustedPAP(PAPData papData) throws RemoteException {
		log.info("addTrustedPAP();");

		try {

			return AddTrustedPAPOperation.instance(papData).execute();

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}
	}

	public boolean exists(String papId) throws RemoteException {
		log.info("exists(" + papId + ");");

		try {

			return TrustedPAPExistsOperation.instance(papId).execute();

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

	public PAPData getTrustedPAP(String papId) throws RemoteException {
		log.info("getTrustedPAP(" + papId + ");");
		try {

			return GetTrustedPAPOperation.instance(papId).execute();

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}
	}

	public PAPData[] listTrustedPAPs() throws RemoteException {
		log.info("listTrustedPAPs();");
		try {

			return ListTrustedPAPsOperation.instance().execute();

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}
	}

	public String ping() throws RemoteException {
		log.info("Requested ping()");
		return "PAP v0.1";
	}

	public boolean refreshCache(String papId) throws RemoteException {
		log.info("refreshCache(" + papId + ");");
		try {

			return RefreshPolicyCacheOperation.instance(papId).execute();

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}
	}

	public boolean removeTrustedPAP(String papId) throws RemoteException {
		log.info("removeTrustedPAP(" + papId + ");");
		try {

			return RemoveTrustedPAPOperation.instance(papId).execute();

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

	public boolean updateTrustedPAP(PAPData papData) throws RemoteException {
		log.info("updateTrustedPAP(" + papData.getPapId() + "," + papData + ");");
		try {

			if (PAP.LOCAL_PAP_ALIAS.equals(papData.getAlias())) {
				throw new PAPManagerException(String.format("Invalid request. \"%s\" cannot be updated",
					PAP.LOCAL_PAP_ALIAS));
			}

			return UpdateTrustedPAPOperation.instance(papData).execute();

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}
	}

}
