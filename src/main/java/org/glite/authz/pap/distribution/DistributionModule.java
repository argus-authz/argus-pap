package org.glite.authz.pap.distribution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PAPPolicySetDAO;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionModule extends Thread {

	private static final Logger log = LoggerFactory
			.getLogger(DistributionModule.class);
	private static DistributionModule instance = null;
	private static PAPPolicySetDAO papDAO;
	private static PolicySetDAO policySetDAO;
	private static PolicyDAO policyDAO;

	public static DistributionModule getInstance() {
		if (instance == null) {
			instance = new DistributionModule();
		}
		return instance;
	}

	private List<RemotePAP> remotePAPs;
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
				papDAO.deleteRemoteAll();
				for (RemotePAP remotePAP : remotePAPs) {
					if (this.isInterrupted()) {
						break;
					}
					List<XACMLObject> papTree = getPoliciesFromPAP(remotePAP);
					storePAPTree(remotePAP, papTree);
				}
				sleep(sleepTime);
			}
		} catch (InterruptedException e) {
		}
		log.info("Shut down distribution module");
	}

	private List<XACMLObject> getPoliciesFromPAP(RemotePAP remotePAP) {
		List<XACMLObject> papPolicies = new LinkedList<XACMLObject>();
		
		// Fake policy generation
		
		log.info("Retrieved policies from: " + remotePAP.getDn());
		return papPolicies;
	}

	private void init() {
		papDAO = DAOFactory.getDAOFactory().getPapDAO();
		policyDAO = DAOFactory.getDAOFactory().getPolicyDAO();
		policySetDAO = DAOFactory.getDAOFactory().getPolicySetDAO();
		sleepTime = 15000;
		remotePAPs = new ArrayList<RemotePAP>(20);
		for (int i = 0; i < 20; i++) {
			remotePAPs.add(new RemotePAP("enpoint", "remote_pap_" + i));
		}
	}

	private void storePAPTree(RemotePAP remotePAP, List<XACMLObject> papTree) {
		String papId = remotePAP.getDn();
		if (papTree.isEmpty()) {
			log.debug("Empty list retrieved from PAP: " + papId);
		}
		XACMLObject papRoot = papTree.get(0);
		if (papRoot instanceof PolicySetType) {
			papDAO.add((PolicySetType) papRoot);
			for (int i=1; i<papTree.size(); i++) {
				XACMLObject ab = papTree.get(i);
				if (ab instanceof PolicySetType) {
					policySetDAO.store(papId, (PolicySetType) ab);
				} else if (ab instanceof PolicyType) {
					policyDAO.store(papId, (PolicyType) ab);
				} else {
					log.error("Invalid AbstractPolicy received from PAP: " + papId);
				}
			}
		} else {
			log.error("Not a PolicySet the root of the policy tree received from PAP: " + papId);
		}
	}
}
