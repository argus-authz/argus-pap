package org.glite.authz.pap.distribution;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionModule extends Thread {

    private static final Logger log = LoggerFactory.getLogger(DistributionModule.class);
    private static DistributionModule instance = null;

    public static DistributionModule getInstance() {
    	if (instance == null)
    		instance= new DistributionModule();
        return instance;
    }

    public static List<XACMLObject> getPoliciesFromPAP(PAP remotePAP) throws RemoteException, ServiceException {

        List<XACMLObject> papPolicies = new LinkedList<XACMLObject>();

        PAPClient client = new PAPClient(remotePAP.getEndpoint());
        papPolicies = client.getLocalPolicies();

        log.info("Retrieved " + papPolicies.size() + " policies from: " + remotePAP.getDn());

        return papPolicies;
    }
    
    public static void refreshCache(PAP pap) throws RemoteException, ServiceException {
        log.info("Refreshing cache for pap: " + pap.getAlias() + "...");
    	List<XACMLObject> papPolicies = getPoliciesFromPAP(pap);
    	log.debug("Received " + papPolicies.size() + " XACML elemenst from PAP \"" + pap.getAlias() + "\"");
        storePAPPolicies(pap, papPolicies);
    }

    private static synchronized void storePAPPolicies(PAP pap, List<XACMLObject> papPolicies) {

        if (papPolicies.isEmpty()) {
            log.debug("Empty list retrieved from PAP: " + pap.getAlias());
            return;
        }

        log.debug("Storing policies for PAP: " + pap.getPapId());

        PAPManager papManager = PAPManager.getInstance();
        PAPContainer papContainer = papManager.getTrustedPAPContainer(pap.getPapId());

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

    private long sleepTime;

    private DistributionModule() {
        initialize();
    }

    public void run() {

        try {
            while (!this.isInterrupted()) {
                
                log.info("Starting refreshing cache process...");
                
                for (PAP pap : PAPManager.getInstance().getAllTrustedPAPs()) {

                    if (this.isInterrupted())
                        break;

                    try {
                        refreshCache(pap);
                    } catch (RemoteException e) {
                        log.error("Cannot connect to: " + pap.getPapId());
                    } catch (ServiceException e) {
                        log.error("Cannot connect to: " + pap.getPapId());
                    }
                    
                }
                
                log.info("Refreshing cache process has finished");
                
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

    protected void initialize() {
        log.info("Initilizing distribution module...");

        sleepTime = DistributionConfiguration.getInstance().getPollIntervallInMillis();
    }
}
