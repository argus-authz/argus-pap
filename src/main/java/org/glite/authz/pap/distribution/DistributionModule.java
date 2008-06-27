package org.glite.authz.pap.distribution;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.PAPManager;
import org.glite.authz.pap.repository.RepositoryManager;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionModule extends Thread {

	private static final String remotePAPConfigurationFile = "/tmp/remote_pap_list.txt";
	private static final Logger log = LoggerFactory.getLogger(DistributionModule.class);
	private static DistributionModule instance = null;

	public static DistributionModule getInstance() {
		if (instance == null) {
			instance = new DistributionModule();
		}
		return instance;
	}

	private List<PAP> remotePAPs = new ArrayList<PAP>();
	private long sleepTime;

	private DistributionModule() {
		init();
	}

	public void end() {
		log.info("Shutting down distribution module...");
		this.interrupt();
		while (this.isAlive());
		log.info("Distribution module stopped");
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
	}

	private List<XACMLObject> getPoliciesFromPAP(PAP remotePAP) {
		List<XACMLObject> papPolicies = new LinkedList<XACMLObject>();
		
		// Fake policy generation... here the client gets the policies from the remote PAP
		papPolicies.add(PolicySetHelper.buildWithAnyTarget(remotePAP.getPapId(), PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS));
		for (int i=0; i<10; i++) {
			papPolicies.add(PolicySetHelper.buildWithAnyTarget(remotePAP.getPapId() + "_Ex_" + i, PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS));
			papPolicies.add(PolicyHelper.buildWithAnyTarget(remotePAP.getPapId() + "_Ex_" + i, PolicyHelper.RULE_COMBALG_DENY_OVERRIDS));
		}
		
		log.info("Retrieved policies from: " + remotePAP.getDn());
		return papPolicies;
	}

	private void init() {
		sleepTime = 15000;
		File configFile = new File(remotePAPConfigurationFile);
		if (configFile.exists()) {
			log.info("Reading remote PAP list configuration file: " + remotePAPConfigurationFile);
			remotePAPs = DistributionConfigurationParser.getInstance().parse(configFile);
			log.info("Found " + remotePAPs.size() + " remote PAPs");
		} else {
			log.info("Remote PAP list configuration file not found: " + remotePAPConfigurationFile);
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
