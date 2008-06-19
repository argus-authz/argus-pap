package org.glite.authz.pap.common.utils.xacml;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xacml.policy.ObligationsType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;

public class PolicySetHelper extends XACMLHelper<PolicySetType> {
	
	public static final String COMB_ALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable";
	public static final String COMB_ALG_ORDERED_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-deny-overrides";
	public static final String COMB_ALG_ORDERED_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-permit-overrides";
	
	private static PolicySetHelper instance = null;

	public static void addPolicyReference(PolicySetType policySet, int index,
			String idReferenceValue) {
		policySet.getPolicyIdReferences().add(
				index,
				IdReferenceHelper.build(
						IdReferenceHelper.Type.POLICY_ID_REFERENCE,
						idReferenceValue));
	}

	public static void addPolicyReference(PolicySetType policySet,
			String idReferenceValue) {
		policySet.getPolicyIdReferences().add(
				IdReferenceHelper.build(
						IdReferenceHelper.Type.POLICY_ID_REFERENCE,
						idReferenceValue));
	}

	public static void addPolicySetReference(PolicySetType policySet,
			int index, String idReferenceValue) {
		policySet.getPolicySetIdReferences().add(
				index,
				IdReferenceHelper.build(
						IdReferenceHelper.Type.POLICYSET_ID_REFERENCE,
						idReferenceValue));
	}

	public static void addPolicySetReference(PolicySetType policySet,
			String idReferenceValue) {
		policySet.getPolicySetIdReferences().add(
				IdReferenceHelper.build(
						IdReferenceHelper.Type.POLICYSET_ID_REFERENCE,
						idReferenceValue));
	}

	private static PolicySetType build() {
		return (PolicySetType) Configuration.getBuilderFactory().getBuilder(
				PolicySetType.DEFAULT_ELEMENT_NAME).buildObject(
				PolicySetType.DEFAULT_ELEMENT_NAME);
	}

	public static PolicySetType build(String policySetId,
			String policyCombinerAlgorithmId, TargetType target) {
		
		return build(policySetId, policyCombinerAlgorithmId, target, null);
	}
	
	public static PolicySetType build(String policySetId,
			String policyCombiningAlgorithmId, TargetType target, ObligationsType obligations) {
		PolicySetType policySet = build();
		policySet.setPolicySetId(policySetId);
		policySet.setPolicyCombiningAlgoId(policyCombiningAlgorithmId);
		if (target == null) {
			policySet.setTarget(TargetHelper.buildAnyTarget());
		} else {
			policySet.setTarget(target);
		}
		if (obligations != null) {
			policySet.setObligations(obligations);
		}
		return policySet;
	}
	
	public static PolicySetType buildWithAnyTarget(String policySetId,
			String policyCombinerAlgorithmId) {
		
		return build(policySetId, policyCombinerAlgorithmId, null, null);
	}

	public static void deletePolicyReference(PolicySetType policySet,
			String policyId) {
		List<IdReferenceType> policyRefList = policySet.getPolicyIdReferences();
		for (IdReferenceType policyRef : policyRefList) {
			if (policyRef.getValue().equals(policyId)) {
				policyRefList.remove(policyRef);
			}
		}
	}

	public static void deletePolicySetReference(PolicySetType policySet,
			String policySetId) {
		List<IdReferenceType> psRefList = policySet.getPolicySetIdReferences();
		for (IdReferenceType psRef : psRefList) {
			if (psRef.getValue().equals(policySetId)) {
				psRefList.remove(psRef);
			}
		}
	}

	private static List<XACMLObject> getChildrenOrderedList(PolicySetType policySet) {
		List<XACMLObject> xacmlObjectChildren = new LinkedList<XACMLObject>();
		List<XMLObject> children = policySet.getOrderedChildren();
		for (XMLObject child : children) {
			if ((child instanceof IdReferenceType)
					|| (child instanceof PolicySetType)
					|| (child instanceof PolicyType)) {
				xacmlObjectChildren.add((XACMLObject) child);
			}
		}
		return xacmlObjectChildren;
	}

	public static PolicySetHelper getInstance() {
		if (instance == null) {
			instance = new PolicySetHelper();
		}
		return instance;
	}
	
	public static int getNumberOfChildren(PolicySetType policySet) {
		return getChildrenOrderedList(policySet).size();
	}
	
	public static List<String> getPolicySetIdReferencesValues(PolicySetType policySet) {
		List<IdReferenceType> refList = policySet.getPolicySetIdReferences();
		List<String> list = new ArrayList<String>(refList.size());
		for (IdReferenceType ref : refList) {
			list.add(ref.getValue());
		}
		return list;
	}

	private static boolean idIsContainedInList(String id, List<IdReferenceType> list) {
		for (IdReferenceType ref : list) {
			if (id.equals(ref.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean policyReferenceIdExists(PolicySetType policySet, String id) {
		return idIsContainedInList(id, policySet.getPolicyIdReferences());
	}

	public static boolean policySetReferenceIdExists(PolicySetType policySet, String id) {
		return idIsContainedInList(id, policySet.getPolicySetIdReferences());
	}
	
	public static boolean referenceIdExists(PolicySetType policySet, String id) {
		for (IdReferenceType ref:policySet.getPolicySetIdReferences()) {
			if (ref.getValue().equals(id)) {
				return true;
			}
		}
		for (IdReferenceType ref:policySet.getPolicyIdReferences()) {
			if (ref.getValue().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	private PolicySetHelper() {
	}

}
