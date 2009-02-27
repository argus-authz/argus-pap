package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.Iterator;

import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagement;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPPolicyIterator implements Iterator<PolicyType> {

    private static final Logger log = LoggerFactory.getLogger(PAPPolicyIterator.class);
    private int curPos = 0;
    private boolean getAllPoliciesInOnce;
    private boolean initialized = false;
    private String papId = null;
    private PolicyType[] policyArray = null;
    private String[] policyIdsArray = null;
    private PolicySetType[] policySetArray = null;

    private XACMLPolicyManagement xacmlPolicyMgmtClient;

    public PAPPolicyIterator(XACMLPolicyManagement xacmlPolicyMgmtClient, String papId, boolean getPoliciesOneByOne) {

        this.xacmlPolicyMgmtClient = xacmlPolicyMgmtClient;
        getAllPoliciesInOnce = !getPoliciesOneByOne;

        if (papId != null) {
            if (papId.length() > 0) {
                this.papId = papId;
            }
        }

    }

    public PolicyType[] getAllPolicies() {
        return policyArray;
    }
    
    public PolicySetType[] getAllPolicySets() {
        return policySetArray;
    }

    public int getNumberOfPolicies() throws RemoteException {

        if (!initialized)
            init();

        int npolicies;

        if (policyArray == null)
            npolicies = policyIdsArray.length;
        else
            npolicies = policyArray.length;

        return npolicies;

    }

    public String[] getPolicyIdsArray() {

        if (!initialized)
            return null;

        String[] idsArray = null;

        if (getAllPoliciesInOnce) {

            int size = policyArray.length;
            idsArray = new String[size];

            for (int i = 0; i < size; i++) {
                idsArray[i] = policyArray[i].getPolicyId();
            }
        } else {
            idsArray = policyIdsArray;
        }

        return idsArray;
    }

    public boolean hasNext() {

        if (!initialized)
            return false;

        if (getAllPoliciesInOnce) {
            if (curPos < policyArray.length)
                return true;
        } else {
            if (curPos < policyIdsArray.length)
                return true;
        }

        return false;
    }

    public void init() throws RemoteException {

        if (!initialized) {

            if (getAllPoliciesInOnce)
                readAllPolicies();
            else
                readPolicyIds();

            initialized = true;
        }
    }

    public PolicyType next() {

        if (!initialized)
            return null;

        PolicyType policy = null;

        if (getAllPoliciesInOnce) {
            policy = policyArray[curPos];
        } else {
            try {
                if (papId == null)
                    policy = xacmlPolicyMgmtClient.getPolicy(policyIdsArray[curPos]);
                else {
                    policy = xacmlPolicyMgmtClient.getPAPPolicy(papId, policyIdsArray[curPos]);
                }
            } catch (RemoteException e) {
                log.error("Cannot retrive policy \"" + policyIdsArray[curPos] + "\"");
                return null;
            }
        }

        curPos++;

        return policy;
    }

    public void remove() {
        // TODO Auto-generated method stub
        log.error("Method remove() not yet implemented");
    }

    private void readAllPolicies() throws RemoteException {

        if (papId == null) {
            policyArray = xacmlPolicyMgmtClient.listPolicies();
            policySetArray = xacmlPolicyMgmtClient.listPolicySets();
        }
        else {
            policyArray = xacmlPolicyMgmtClient.listPAPPolicies(papId);
            policySetArray = xacmlPolicyMgmtClient.listPAPPolicySets(papId);
        }

    }

    private void readPolicyIds() throws RemoteException {
        PolicySetType[] policySetArray;

        if (papId == null)
            policySetArray = xacmlPolicyMgmtClient.listPolicySets();
        else
            policySetArray = xacmlPolicyMgmtClient.listPAPPolicySets(papId);

        int npolicy = 0;
        for (PolicySetType policySet : policySetArray) {
            npolicy += policySet.getPolicyIdReferences().size();
        }

        policyIdsArray = new String[npolicy];

        int i = 0;
        for (PolicySetType policySet : policySetArray) {
            for (IdReferenceType refId : policySet.getPolicyIdReferences()) {
                policyIdsArray[i] = refId.getValue();
                i++;
            }
        }
    }

}
