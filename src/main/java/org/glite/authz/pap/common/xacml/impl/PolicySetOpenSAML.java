package org.glite.authz.pap.common.xacml.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.AbstractPolicy;
import org.glite.authz.pap.common.xacml.IdReference;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.exceptions.XACMLException;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PolicySetOpenSAML extends PolicySet {

	private PolicySetType policySet;

	public PolicySetOpenSAML(Element element) {
		UnmarshallerFactory unmarshallerFactory = Configuration
				.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory
				.getUnmarshaller(element);
		try {
			policySet = (PolicySetType) unmarshaller.unmarshall(element);
		} catch (UnmarshallingException e) {
			throw new XACMLException(e);
		}
	}

	public PolicySetOpenSAML(PolicySetType openSAMLPolicySet) {
		policySet = openSAMLPolicySet;
	}

	@SuppressWarnings("unchecked")
	public PolicySetOpenSAML(String policySetId,
			String policyCombinerAlgorithmId) {
		XMLObjectBuilder<PolicySetType> policySetBuilder = Configuration
				.getBuilderFactory()
				.getBuilder(
						org.opensaml.xacml.policy.PolicySetType.DEFAULT_ELEMENT_NAME);
		policySet = policySetBuilder
				.buildObject(org.opensaml.xacml.policy.PolicySetType.DEFAULT_ELEMENT_NAME);
		policySet.setPolicySetId(policySetId);
		policySet.setTarget(new TargetOpenSAML().getOpenSAMLTargetType());
		policySet.setPolicyCombiningAlgoId(policyCombinerAlgorithmId);
	}

	public void addPolicyReference(int index, String value) {
		policySet.getPolicyIdReferences().add(
				0,
				new IdReferenceOpenSAML(IdReference.Type.POLICYIDREFERENCE,
						value).getOpenSAMLObject());
	}

	public void addPolicyReference(String value) {
		policySet.getPolicyIdReferences().add(
				new IdReferenceOpenSAML(IdReference.Type.POLICYIDREFERENCE,
						value).getOpenSAMLObject());
	}

	public void addPolicySetReference(int index, String value) {
		policySet.getPolicySetIdReferences().add(
				index,
				new IdReferenceOpenSAML(IdReference.Type.POLICYSETIDREFERENCE,
						value).getOpenSAMLObject());
	}

	public void addPolicySetReference(String value) {
		policySet.getPolicySetIdReferences().add(
				new IdReferenceOpenSAML(IdReference.Type.POLICYSETIDREFERENCE,
						value).getOpenSAMLObject());
	}

	public void deletePolicyReference(String policyId) {
		List<IdReferenceType> policyRefList = policySet.getPolicyIdReferences();
		for (IdReferenceType policyRef:policyRefList) {
			if (policyRef.getValue().equals(policyId)) {
				policyRefList.remove(policyRef);
			}
		}
	}

	public void deletePolicySetReference(String policySetId) {
		List<IdReferenceType> psRefList = policySet.getPolicySetIdReferences();
		for (IdReferenceType psRef:psRefList) {
			if (psRef.getValue().equals(policySetId)) {
				psRefList.remove(psRef);
			}
		}
	}

	public Node getDOM() {
		MarshallerFactory marshallerFactory = Configuration
				.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(policySet);
		try {
			marshaller.marshall(policySet);
		} catch (MarshallingException e) {
			throw new XACMLException(e);
		}
		return policySet.getDOM();
	}

	public String getId() {
		return policySet.getPolicySetId();
	}

	public int getNumberOfChildren() {
		return getOrderedChildren().size();
	}

	public List<AbstractPolicy> getOrderedListOfChildren() {
		return getOrderedChildren();
	}

	public List<String> getPolicySetIdReferencesValues() {
		List<IdReferenceType> refList = policySet.getPolicySetIdReferences();
		List<String> list = new ArrayList<String>(refList.size());
		for (IdReferenceType ref : refList) {
			list.add(ref.getValue());
		}
		return list;
	}

	public boolean policyReferenceIdExists(String id) {
		return idIsContainedInList(id, policySet.getPolicyIdReferences());
	}

	public boolean policySetReferenceIdExists(String id) {
		return idIsContainedInList(id, policySet.getPolicySetIdReferences());
	}

	public boolean referenceIdExists(String id) {
		List<AbstractPolicy> children = getOrderedChildren();
		for (AbstractPolicy child : children) {
			if (child instanceof IdReference) {
				if (((IdReference) child).getValue().equals(id)) {
					return true;
				}
			}
		}
		return false;
	}

	public void setId(String policySetId) {
		policySet.setPolicySetId(policySetId);
	}

	private List<AbstractPolicy> getOrderedChildren() {
		List<AbstractPolicy> xacmlObjectChildren = new LinkedList<AbstractPolicy>();
		List<XMLObject> children = policySet.getOrderedChildren();
		for (XMLObject child : children) {
			if (child instanceof IdReferenceType) {
				xacmlObjectChildren.add(new IdReferenceOpenSAML(
						(IdReferenceType) child));
			} else if (child instanceof PolicySetType) {
				xacmlObjectChildren.add(new PolicySetOpenSAML(
						(PolicySetType) child));
			} else if (child instanceof PolicyType) {
				// xacmlObjectChildren.add(new PolicySetOpenSAML((PolicySetType)
				// child));
			}
		}
		return xacmlObjectChildren;
	}

	private boolean idIsContainedInList(String id, List<IdReferenceType> list) {
		for (IdReferenceType ref : list) {
			if (id.equals(ref.getValue())) {
				return true;
			}
		}
		return false;
	}
}
