package org.glite.authz.pap.distribution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.provisioning.client.ProvisioningServiceClient;
import org.glite.authz.pap.provisioning.client.ProvisioningServiceClientFactory;
import org.glite.authz.pap.provisioning.client.ProvisioningServicePortType;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.PAPManager;
import org.glite.authz.pap.repository.RepositoryManager;
import org.opensaml.saml2.core.Response;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionModule extends Thread {

	private static final Logger log = LoggerFactory.getLogger(DistributionModule.class);
	private static DistributionModule instance = new DistributionModule();
	private final ProvisioningServiceClient client;

	public static DistributionModule getInstance() {
		return instance;
	}

	private List<PAP> remotePAPs = new ArrayList<PAP>();
	private long sleepTime;

	public DistributionModule() {
	    DistributionConfigurationParser conf = DistributionConfigurationParser.getInstance();
	    sleepTime = conf.getPollingInterval();
	    remotePAPs = conf.getRemotePAPs();
	    
	    ProvisioningServiceClientFactory factory = ProvisioningServiceClientFactory.getProvisioningServiceClientFactory();
        client = factory.createPolicyProvisioningServiceClient();
        
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
		
		ProvisioningServicePortType port = client.getProvisioningServicePortType("http://example.org/ProvisioningService");
		
		XACMLPolicyQueryType query;
		
		//Response response = port.xacmlPolicyQuery(xacmlPolicyQueryType);
		
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
