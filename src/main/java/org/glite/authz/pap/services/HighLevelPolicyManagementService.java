package org.glite.authz.pap.services;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.BlacklistPolicySet;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.ServiceClassPolicySet;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HighLevelPolicyManagementService implements HighLevelPolicyManagement {

	private static final Logger log = LoggerFactory.getLogger(HighLevelPolicyManagementService.class);

	public String banDN(String dn, boolean isPublic, String description) throws RemoteException {
		log.info("Received banDN() request (dn=" + dn + ", isPublic=" + isPublic);

		try {

			String policyId = null;
			policyId = banAttribute(AttributeWizardType.DN, dn, isPublic, description);
			log.info("Created BlackList policy: " + policyId);
			return policyId;

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}

	}

	public String banFQAN(String fqan, boolean isPublic, String description) throws RemoteException {
		log.info("Received banFQAN() request (fqan=" + fqan + ", isPublic=" + isPublic);

		try {

			String policyId = null;
			policyId = banAttribute(AttributeWizardType.FQAN, fqan, isPublic, description);
			log.info("Created BlackList policy: " + policyId);
			return policyId;

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}

	}

	public String dnJobPriority(String dn, String serviceClass, boolean isPublic, String description)
			throws RemoteException {
		log.info("Received dnJobPriority() request (dn=" + dn + ", serviceClass=" + serviceClass
				+ ", isPublic=" + isPublic);
		try {

			String policyId = null;
			policyId = jobPriorityPolicy(AttributeWizardType.DN, dn, serviceClass, isPublic, description);
			log.info("Created ServiceClass policy: " + policyId);
			return policyId;

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}
	}

	public String fqanJobPriority(String fqan, String serviceClass, boolean isPublic, String description)
			throws RemoteException {

		log.info("Received fqanJobPriority() request (fqan=" + fqan + ", serviceClass=" + serviceClass
				+ ", isPublic=" + isPublic);

		try {

			String policyId = null;
			policyId = jobPriorityPolicy(AttributeWizardType.FQAN, fqan, serviceClass, isPublic, description);
			log.info("Created ServiceClass policy: " + policyId);
			return policyId;

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}
	}

	public UnbanResult unbanDN(String dn) throws RemoteException {
		log.info("Received unbanDN() request (dn=" + dn + ")");

		try {

			UnbanResult unbanResult = null;
			unbanResult = unbanAttribute(AttributeWizardType.DN, dn);
			if (unbanResult.getStatusCode() == 0)
				log.info("dn=\"" + dn + "\" successfully unbanned");
			else
				log.info("dn=\"" + dn + "\" NOT unbanned");
			return unbanResult;

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}
	}

	public UnbanResult unbanFQAN(String fqan) throws RemoteException {

		log.info("Received unbanFQAN() request (fqan=" + fqan + ")");

		try {

			UnbanResult unbanResult = null;
			unbanResult = unbanAttribute(AttributeWizardType.FQAN, fqan);
			if (unbanResult.getStatusCode() == 0)
				log.info("fqan=\"" + fqan + "\" successfully unbanned");
			else
				log.info("fqan" + fqan + "\" NOT unbanned");
			return unbanResult;

		} catch (RuntimeException e) {
			ServiceClassExceptionManager.log(log, e);
			throw e;
		}

	}

	private String banAttribute(AttributeWizardType attributeWizardType, String attributeValue,
			boolean isPublic, String description) {

		List<AttributeWizard> targetList = new LinkedList<AttributeWizard>();
		targetList.add(new AttributeWizard(attributeWizardType, attributeValue));
		targetList.add(new AttributeWizard(AttributeWizardType.RESOURCE_URI, "*"));
		PolicyWizard policyWizard = new PolicyWizard(targetList, null, EffectType.Deny);
		if (isPublic)
			policyWizard.setPrivate(false);
		else
			policyWizard.setPrivate(true);
		if (description != null)
			if (description.length() > 0)
				policyWizard.setDescription(description);
		PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
		localPAP.addPolicy(BlacklistPolicySet.POLICY_SET_ID, policyWizard.getPolicyType());
		return policyWizard.getPolicyType().getPolicyId();
	}

	private String jobPriorityPolicy(AttributeWizardType attributeWizardType, String attributeValue,
			String serviceClass, boolean isPublic, String description) {

		List<AttributeWizard> targetList = new LinkedList<AttributeWizard>();
		targetList.add(new AttributeWizard(attributeWizardType, attributeValue));
		targetList.add(new AttributeWizard(AttributeWizardType.RESOURCE_URI, "*"));
		targetList.add(new AttributeWizard(AttributeWizardType.SERVICE_CLASS, serviceClass));
		PolicyWizard policyWizard = new PolicyWizard(targetList, null, EffectType.Permit);
		if (isPublic)
			policyWizard.setPrivate(false);
		else
			policyWizard.setPrivate(true);
		if (description != null)
			if (description.length() > 0)
				policyWizard.setDescription(description);
		PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
		localPAP.addPolicy(ServiceClassPolicySet.POLICY_SET_ID, policyWizard.getPolicyType());
		return policyWizard.getPolicyType().getPolicyId();
	}

	private UnbanResult unbanAttribute(AttributeWizardType attributeType, String attributeValue) {

		PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

		List<PolicyType> policyList = localPAP.getAllPolicies();

		boolean noPoliciesRemoved = true;

		for (PolicyType policy : policyList) {

			PolicyWizard policyWizard = new PolicyWizard(policy);

			if (AttributeWizardType.DN.equals(attributeType)) {

				if (policyWizard.isBanPolicyForDN(attributeValue)) {
					localPAP.removePolicyAndReferences(policy.getPolicyId());
					noPoliciesRemoved = false;
				}
			} else {

				if (policyWizard.isBanPolicyForFQAN(attributeValue)) {
					localPAP.removePolicyAndReferences(policy.getPolicyId());
					noPoliciesRemoved = false;
				}
			}
		}

		UnbanResult unbanResult = new UnbanResult();
		unbanResult.setConflictingPolicies(new String[0]);

		if (noPoliciesRemoved)
			unbanResult.setStatusCode(1);
		else
			unbanResult.setStatusCode(0);

		return unbanResult;
	}
}
