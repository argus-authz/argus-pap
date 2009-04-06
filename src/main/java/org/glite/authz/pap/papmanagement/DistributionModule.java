package org.glite.authz.pap.papmanagement;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionModule extends Thread {

    public static final Object storePoliciesLock = new Object();

    private static DistributionModule instance = null;
    private static final Logger log = LoggerFactory.getLogger(DistributionModule.class);

    private long sleepTime;

    private DistributionModule() {
        initialize();
    }

    public static DistributionModule getInstance() {
        if (instance == null)
            instance = new DistributionModule();
        return instance;
    }

    public static List<XACMLObject> getPoliciesFromPAP(Pap remotePAP) throws RemoteException, ServiceException {

        PAPClient client = new PAPClient(remotePAP.getEndpoint());

        List<XACMLObject> papPolicies = client.getLocalPolicies();

        return papPolicies;
    }

    public static void refreshCache(Pap pap) throws RemoteException, ServiceException {
        log.info("Refreshing cache of remote PAP " + pap.getAlias());
        List<XACMLObject> papPolicies = getPoliciesFromPAP(pap);
        log.info(String.format("Retrieved %d XACML objects from PAP %s (%s)",
                               papPolicies.size(),
                               pap.getAlias(),
                               pap.getEndpoint()));
        storePAPPolicies(pap, papPolicies);
    }

    private static void storePAPPolicies(Pap pap, List<XACMLObject> papPolicies) {

        if (papPolicies.isEmpty()) {
            return;
        }

        log.debug(String.format("Storing policies for PAP %s (id=%s)", pap.getAlias(), pap.getId()));

        PapManager papManager = PapManager.getInstance();
        PapContainer papContainer = papManager.getPapContainer(pap.getAlias());

        synchronized (storePoliciesLock) {

            papContainer.deleteAllPolicies();
            papContainer.deleteAllPolicySets();

            XACMLObject papRoot = papPolicies.get(0);

            if (papRoot instanceof PolicySetType) {

                ((PolicySetType) papRoot).setPolicySetId(papContainer.getPAP().getId());

                for (XACMLObject xacmlObject : papPolicies) {

                    if (xacmlObject instanceof PolicySetType) {
                        PolicySetType policySet = (PolicySetType) xacmlObject;
                        papContainer.storePolicySet(policySet);

                        log.debug(String.format("Stored PolicySet \"%s\" into pap \"%s\"",
                                                policySet.getPolicySetId(),
                                                pap.getAlias()));
                    } else if (xacmlObject instanceof PolicyType) {

                        PolicyType policy = (PolicyType) xacmlObject;
                        papContainer.storePolicy(policy);

                        log.debug(String.format("Stored Policy \"%s\" into pap \"%s\"", policy.getPolicyId(), pap.getAlias()));
                    } else {

                        log.error(String.format("Invalid object (not a Policy or PolicySet) received from PAP %s (%s)",
                                                pap.getAlias(),
                                                pap.getEndpoint()));
                    }
                    TypeStringUtils.releaseUnneededMemory(xacmlObject);
                }
            } else {
                log.error(String.format("The root of the policy tree is not a PolicySet (papAlias=%s, endpoint=%s)",
                                        pap.getAlias(),
                                        pap.getEndpoint()));
            }
        }
    }

    public void run() {

        try {
            while (!this.isInterrupted()) {

                log.info("Starting refreshing cache process...");

                for (Pap pap : PapManager.getInstance().getOrderedRemotePaps()) {

                    if (this.isInterrupted())
                        break;

                    try {
                        refreshCache(pap);
                    } catch (RemoteException e) {
                        log.error(String.format("Error connecting to %s (%s): %s", pap.getAlias(), pap.getEndpoint(), e.getMessage()));
                    } catch (ServiceException e) {
                        log.error(String.format("Cannot connect to: %s (%s)", pap.getAlias(), pap.getEndpoint(), e.getMessage()));
                    }

                }

                log.info("Refreshing cache process has finished");

                sleep(sleepTime);
            }
        } catch (InterruptedException e) {}
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

    protected void initialize() {
        log.info("Initilizing distribution module...");

        sleepTime = DistributionConfiguration.getInstance().getPollIntervallInMilliSecs();
    }

}
