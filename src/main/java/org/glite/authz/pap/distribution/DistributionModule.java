package org.glite.authz.pap.distribution;

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
    private static DistributionModule instance = new DistributionModule();

    public static DistributionModule getInstance() {
        return instance;
    }

    private List<PAP> remotePAPList;
    private long sleepTime;

    private DistributionModule() {
        initialize();
    }

    public List<XACMLObject> getPoliciesFromPAP(PAP remotePAP) {

        List<XACMLObject> papPolicies = new LinkedList<XACMLObject>();

        PAPClient client = new PAPClient(remotePAP.getEndpoint());
        papPolicies = client.getLocalPolicies();

        log.info("Retrieved " + papPolicies.size() + " policies from: " + remotePAP.getDn());

        return papPolicies;
    }

    public void run() {

        try {
            while (!this.isInterrupted()) {
                for (PAP pap : remotePAPList) {

                    if (this.isInterrupted())
                        break;

                    List<XACMLObject> papPolicies = getPoliciesFromPAP(pap);
                    storePAPPolicies(pap, papPolicies);
                }
                sleep(sleepTime);
            }
        } catch (InterruptedException e) {
        }
    }

    public void startDistributionModule() {
        this.start();
    }

    public void stopDistributionModule() {

        log.info("Shutting down distribution module...");
        this.interrupt();

        while (this.isAlive());

        log.info("Distribution module stopped");
    }

    private void storePAPPolicies(PAP pap, List<XACMLObject> papPolicies) {

        if (papPolicies.isEmpty()) {
            log.debug("Empty list retrieved from PAP: " + pap.getDn());
            return;
        }

        log.debug("Storing policies for PAP: " + pap.getPapId());

        PAPManager papManager = RepositoryManager.getPAPManager();
        PAPContainer papContainer = papManager.get(pap);

        papContainer.deleteAllPolicies();
        papContainer.deleteAllPolicySets();

        XACMLObject papRoot = papPolicies.get(0);

        if (papRoot instanceof PolicySetType) {

            ((PolicySetType) papRoot).setPolicySetId(papContainer.getPAP().getPapId());

            for (XACMLObject xacmlObject : papPolicies) {

                if (xacmlObject instanceof PolicySetType) {
                    log.debug("Storing PolicySet into PAP: " + pap.getPapId());
                    papContainer.storePolicySet((PolicySetType) xacmlObject);
                } else if (xacmlObject instanceof PolicyType) {
                    log.debug("Storing Policy into PAP: " + pap.getPapId());
                    papContainer.storePolicy((PolicyType) xacmlObject);
                } else {
                    log.error("Invalid object (not a Policy or PolicySet) received from PAP: "
                            + pap.getDn());
                }

            }
        } else {
            log.error("Not a PolicySet the root of the policy tree received from PAP: " + pap.getDn());
        }
    }

    protected void initialize() {
        log.info("Initilizing distribution module...");

        DistributionConfiguration configuration = DistributionConfiguration.getInstance();

        sleepTime = configuration.getPollIntervallInMillis();
        remotePAPList = configuration.getRemotePAPList();

        // Create non-existing PAPs
        PAPManager papManager = RepositoryManager.getPAPManager();
        for (PAP pap : remotePAPList) {
            if (!papManager.exists(pap)) {
                log.debug("Creating new PAP: " + pap.getPapId());
                papManager.create(pap);
            }
        }

    }
}
