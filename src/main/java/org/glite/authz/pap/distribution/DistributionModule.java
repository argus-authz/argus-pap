package org.glite.authz.pap.distribution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.PAPManager;
import org.glite.authz.pap.repository.RepositoryManager;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionModule extends Thread {

	private static final Logger log = LoggerFactory.getLogger(DistributionModule.class);
	private static DistributionModule instance = null;

	public static DistributionModule getInstance() {
		if (instance == null) {
			instance = new DistributionModule();
		}
		return instance;
	}

	private List<PAP> remotePAPs;
	private long sleepTime;

	private DistributionModule() {
		init();
	}

	public void end() {
		this.interrupt();
		while (this.isAlive());
	}

	public void run() {
		try {
			while (!this.isInterrupted()) {
				for (PAP remotePAP : remotePAPs) {
					if (this.isInterrupted()) {
						break;
					}
					List<XACMLObject> papTree = getPoliciesFromPAP(remotePAP);
					storePAPPolicies(remotePAP, papTree);
				}
				sleep(sleepTime);
			}
		} catch (InterruptedException e) {
		}
		log.info("Shut down distribution module");
	}

	private List<XACMLObject> getPoliciesFromPAP(PAP remotePAP) {
		List<XACMLObject> papPolicies = new LinkedList<XACMLObject>();
		
		// Fake policy generation
		
		log.info("Retrieved policies from: " + remotePAP.getDn());
		return papPolicies;
	}

	private void init() {
		sleepTime = 15000;
		remotePAPs = new ArrayList<PAP>(20);
		for (int i = 0; i < 20; i++) {
			remotePAPs.add(new PAP("remote_pap_" + i, "enpoint", "dn"));
		}
	}

	private void storePAPPolicies(PAP remotePAP, List<XACMLObject> papPolicies) {
		String papId = remotePAP.getDn();
		if (papPolicies.isEmpty()) {
			log.debug("Empty list retrieved from PAP: " + papId);
		}
		PAPContainer papContainer = null;
		PAPManager papManager = RepositoryManager.getPAPManager();
		if (!papManager.exists(remotePAP)) {
			papContainer = papManager.create(remotePAP);
		} else {
			papContainer = papManager.get(remotePAP);
			papContainer.deleteAllPolicies();
			papContainer.deleteAllPolicySets();
		}
		XACMLObject papRoot = papPolicies.get(0);
		if (papRoot instanceof PolicySetType) {
			((PolicySetType) papRoot).setPolicySetId(papContainer.getPAP().getPapId());
			for (XACMLObject xacmlObject: papPolicies) {
				if (xacmlObject instanceof PolicySetType) {
					papContainer.storePolicySet((PolicySetType) xacmlObject);
				} else if (xacmlObject instanceof PolicyType) {
					papContainer.storePolicy((PolicyType) xacmlObject);
				} else {
					log.error("Invalid AbstractPolicy received from PAP: " + papId);
				}
			}
		} else {
			log.error("Not a PolicySet the root of the policy tree received from PAP: " + papId);
		}
	}
}
