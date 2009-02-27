package org.glite.authz.pap.common.xacml.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xacml.policy.ObligationsType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xml.XMLObject;

public class PolicySetHelper extends XMLObjectHelper<PolicySetType> {

    public static final String COMB_ALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable";
    public static final String COMB_ALG_ORDERED_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-deny-overrides";
    public static final String COMB_ALG_ORDERED_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-permit-overrides";
    private static final javax.xml.namespace.QName ELEMENT_NAME = PolicySetType.DEFAULT_ELEMENT_NAME;

    private static PolicySetHelper instance = new PolicySetHelper();

    private PolicySetHelper() {}

    public static void addPolicy(PolicySetType policySet, int index, PolicyType policy) {
        policySet.getPolicies().add(index, policy);
    }

    public static void addPolicy(PolicySetType policySet, PolicyType policy) {
        policySet.getPolicies().add(policy);
    }

    public static void addPolicyReference(PolicySetType policySet, int index, String idValue) {
        policySet.getPolicyIdReferences()
                .add(index, IdReferenceHelper.build(IdReferenceHelper.Type.POLICY_ID_REFERENCE, idValue));
    }

    public static void addPolicyReference(PolicySetType policySet, String idValue) {
        policySet.getPolicyIdReferences().add(IdReferenceHelper.build(IdReferenceHelper.Type.POLICY_ID_REFERENCE, idValue));
    }

    public static void addPolicySet(PolicySetType policySet, int index, PolicySetType childPolicySet) {
        policySet.getPolicySets().add(index, childPolicySet);
    }

    public static void addPolicySet(PolicySetType policySet, PolicySetType childPolicySet) {
        policySet.getPolicySets().add(childPolicySet);
    }

    public static void addPolicySetReference(PolicySetType policySet, int index, String idValue) {
        policySet.getPolicySetIdReferences().add(index,
                IdReferenceHelper.build(IdReferenceHelper.Type.POLICYSET_ID_REFERENCE, idValue));
    }

    public static void addPolicySetReference(PolicySetType policySet, String idValue) {
        policySet.getPolicySetIdReferences().add(IdReferenceHelper.build(IdReferenceHelper.Type.POLICYSET_ID_REFERENCE, idValue));
    }

    public static PolicySetType build(String policySetId, String policyCombinerAlgorithmId, TargetType target) {

        return build(policySetId, policyCombinerAlgorithmId, target, null);
    }

    public static PolicySetType build(String policySetId, String policyCombiningAlgorithmId, TargetType target,
            ObligationsType obligations) {
        PolicySetType policySet = build();
        policySet.setPolicySetId(policySetId);
        policySet.setPolicyCombiningAlgoId(policyCombiningAlgorithmId);
        if (target == null) {
            policySet.setTarget(TargetHelper.build());
        } else {
            policySet.setTarget(target);
        }
        if (obligations != null) {
            policySet.setObligations(obligations);
        }
        return policySet;
    }

    public static PolicySetType buildWithAnyTarget(String policySetId, String policyCombinerAlgorithmId) {

        return build(policySetId, policyCombinerAlgorithmId, null, null);
    }

    public static boolean changePolicyReferenceValue(PolicySetType policySet, String oldValue, String newValue) {

        List<String> policyIdList = getPolicyIdReferencesValues(policySet);

        for (int i = 0; i < policyIdList.size(); i++) {

            if (policyIdList.get(i).equals(oldValue)) {
                policySet.getPolicyIdReferences().set(i,
                        IdReferenceHelper.build(IdReferenceHelper.Type.POLICY_ID_REFERENCE, newValue));
                return true;
            }
        }

        return false;
    }

    public static boolean deletePolicyReference(PolicySetType policySet, String policyId) {
        List<IdReferenceType> policyRefList = policySet.getPolicyIdReferences();
        for (IdReferenceType policyRef : policyRefList) {
            if (policyRef.getValue().equals(policyId)) {
                policyRefList.remove(policyRef);
                return true;
            }
        }
        return false;
    }

    public static boolean deletePolicySetReference(PolicySetType policySet, String policySetId) {
        List<IdReferenceType> psRefList = policySet.getPolicySetIdReferences();
        for (IdReferenceType psRef : psRefList) {
            if (psRef.getValue().equals(policySetId)) {
                psRefList.remove(psRef);
                return true;
            }
        }
        return false;
    }

    public static PolicySetHelper getInstance() {
        return instance;
    }

    public static int getNumberOfChildren(PolicySetType policySet) {
        return getChildrenOrderedList(policySet).size();
    }

    public static List<String> getPolicyIdReferencesValues(PolicySetType policySet) {
        List<IdReferenceType> refList = policySet.getPolicyIdReferences();
        List<String> list = new ArrayList<String>(refList.size());
        for (IdReferenceType ref : refList) {
            list.add(ref.getValue());
        }
        return list;
    }

    public static List<String> getPolicySetIdReferencesValues(PolicySetType policySet) {
        List<IdReferenceType> refList = policySet.getPolicySetIdReferences();
        List<String> list = new ArrayList<String>(refList.size());
        for (IdReferenceType ref : refList) {
            list.add(ref.getValue());
        }
        return list;
    }

    public static boolean hasPolicyReferenceId(PolicySetType policySet, String id) {
        return idIsContainedInList(id, policySet.getPolicyIdReferences());
    }

    public static boolean hasPolicySetReferenceId(PolicySetType policySet, String id) {
        return idIsContainedInList(id, policySet.getPolicySetIdReferences());
    }

    public static boolean referenceIdExists(PolicySetType policySet, String id) {
        for (IdReferenceType ref : policySet.getPolicySetIdReferences()) {
            if (ref.getValue().equals(id)) {
                return true;
            }
        }
        for (IdReferenceType ref : policySet.getPolicyIdReferences()) {
            if (ref.getValue().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static PolicySetType build() {
        return (PolicySetType) builderFactory.getBuilder(ELEMENT_NAME).buildObject(ELEMENT_NAME);
    }

    private static List<XACMLObject> getChildrenOrderedList(PolicySetType policySet) {
        List<XACMLObject> xacmlObjectChildren = new LinkedList<XACMLObject>();
        List<XMLObject> children = policySet.getOrderedChildren();
        for (XMLObject child : children) {
            if ((child instanceof IdReferenceType) || (child instanceof PolicySetType) || (child instanceof PolicyType)) {
                xacmlObjectChildren.add((XACMLObject) child);
            }
        }
        return xacmlObjectChildren;
    }

    private static boolean idIsContainedInList(String id, List<IdReferenceType> list) {
        for (IdReferenceType ref : list) {
            if (id.equals(ref.getValue())) {
                return true;
            }
        }
        return false;
    }

}
