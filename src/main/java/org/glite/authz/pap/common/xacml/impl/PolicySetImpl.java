package org.glite.authz.pap.common.xacml.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.InvalidPolicySet_TO_DELETE;
import org.glite.authz.pap.common.utils.xacml.XACMLException;
import org.glite.authz.pap.common.xacml.AbstractPolicy;
import org.glite.authz.pap.common.xacml.IdReference;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PolicySetImpl extends PolicySet {

	private Node policySetDOM;
	private Node attributeId;
	
	public PolicySetImpl(Document doc) {
		init(doc);
	}
	
	public void addPolicyReference(int index, String value) {
		if (policyReferenceIdExists(value)) {
			throw new XACMLException("Reference already exists");
		}
		Document doc = policySetDOM.getOwnerDocument();
		Element ref = doc.createElementNS(null, "PolicyIdReference");
		ref.setTextContent(value);
		Node childAtIndex = getChildAtIndex(index);
		if (childAtIndex == null) {
			policySetDOM.appendChild(ref);
		} else {
			policySetDOM.insertBefore(ref, childAtIndex);
		}
	}
	
	public void addPolicyReference(String value) {
		// TODO Auto-generated method stub
		
	}

	public void addPolicySetReference(int index, String value) {
		if (policySetReferenceIdExists(value)) {
			throw new XACMLException("Reference already exists");
		}
		Document doc = policySetDOM.getOwnerDocument();
		Element ref = doc.createElementNS(null, "PolicySetIdReference");
		ref.setTextContent(value);
		Node childAtIndex = getChildAtIndex(index);
		if (childAtIndex == null) {
			policySetDOM.appendChild(ref);
		} else {
			policySetDOM.insertBefore(ref, childAtIndex);
		}
	}

	public void addPolicySetReference(String value) {
		// TODO Auto-generated method stub
		
	}

	public void deletePolicyReference(String policyId) {
		List<AbstractPolicy> children = getOrderedListOfChildren();
		for (AbstractPolicy child:children) {
			if (isPolicyReference(child)) {
				if (policyId.equals(((IdReference) child).getValue())) {
					policySetDOM.removeChild(child.getDOM());
				}
			}
		}
	}

	public void deletePolicySetReference(String policySetId) {
		List<AbstractPolicy> children = getOrderedListOfChildren();
		for (AbstractPolicy child:children) {
			if (isPolicySetReference(child)) {
				if (policySetId.equals(((IdReference) child).getValue())) {
					policySetDOM.removeChild(child.getDOM());
				}
			}
		}
	}

	public Node getDOM() {
		return this.policySetDOM;
	}
	
	public String getId() {
		return attributeId.getNodeValue();
	}

	public int getNumberOfChildren() {
		return getOrderedListOfChildren().size();
	}

	public List<AbstractPolicy> getOrderedListOfChildren() {
		List<AbstractPolicy> result = new LinkedList<AbstractPolicy>();
		NodeList nodeList = policySetDOM.getChildNodes();
		Node node = null;
		for (int i=0; i<nodeList.getLength(); i++) {
			node = nodeList.item(i);
			String nodeName = node.getLocalName();
			if (nodeName != null) {
				if ("PolicySet".equals(nodeName)) {
					result.add(new PolicySetImpl(node.getOwnerDocument()));
				} else if ("Policy".equals(nodeName)) {
					// TODO
				} else if ("PolicySetIdReference".equals(nodeName)) {
					result.add(new IdReferenceImpl(IdReference.Type.POLICYSETIDREFERENCE, node.getTextContent(), node));
				} else if ("PolicyIdReference".equals(nodeName)) {
					result.add(new IdReferenceImpl(IdReference.Type.POLICYIDREFERENCE, node.getTextContent(), node));
				}
			}
		}
		return result;
	}
	
	public List<String> getPolicySetIdReferencesValues() {
		List<AbstractPolicy> list = getOrderedListOfChildren();
		List<String> result = new ArrayList<String>(list.size());
		for (AbstractPolicy ap:list) {
			if (ap instanceof IdReference) {
				IdReference ref = (IdReference) ap;
				if (ref.isPolicySetReference()) {
					result.add(ref.getValue());
				}
			}
		}
		return result;
	}

	public boolean policyReferenceIdExists(String id) {
		List<AbstractPolicy> children = getOrderedListOfChildren();
		for (AbstractPolicy child:children) {
			if (isPolicyReference(child)) {
				if (id.equals(((IdReference) child).getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean policySetReferenceIdExists(String id) {
		List<AbstractPolicy> children = getOrderedListOfChildren();
		for (AbstractPolicy child:children) {
			if (isPolicySetReference(child)) {
				if (id.equals(((IdReference) child).getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean referenceIdExists(String id) {
		List<AbstractPolicy> children = getOrderedListOfChildren();
		for (AbstractPolicy child:children) {
			if (child instanceof IdReference) {
				if (id.equals(((IdReference) child).getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	public void setId(String policySetId) {
		attributeId.setNodeValue(policySetId);
	}

	private Node getChildAtIndex(int index) {
		NodeList nodeList = policySetDOM.getChildNodes();
		Node node = null;
		for (int i=0, j=0; i<nodeList.getLength(); i++) {
			node = nodeList.item(i);
			String nodeName = node.getLocalName();
			if (nodeName != null) {
				if ("PolicySet".equals(nodeName)) {
					if (j == index) {
						return node;
					}
					j++;
				} else if ("Policy".equals(nodeName)) {
					if (j == index) {
						return node;
					}
					j++;
				} else if ("PolicySetIdReference".equals(nodeName)) {
					if (j == index) {
						return node;
					}
					j++;
				} else if ("PolicyIdReference".equals(nodeName)) {
					if (j == index) {
						return node;
					}
					j++;
				}
			}
		}
		return null;
	}

	private Node getDOMPolicySetAttributeId(Document doc) {
		Node attributeId = null;
		NodeList nodeList = doc.getChildNodes();
		Node element = null;
		for (int i=0; i<nodeList.getLength(); i++) {
			element = nodeList.item(i);
			String nodeName = element.getLocalName();
			if ("PolicySet".equals(nodeName)) {
				attributeId = element.getAttributes().getNamedItem("PolicySetId");
				break;
			}
		}
		if ((element == null) || (attributeId == null)) {
			throw new InvalidPolicySet_TO_DELETE();
		}
		return attributeId;
	}

	private void init(Document doc) {
		policySetDOM = doc.getDocumentElement();
		attributeId = getDOMPolicySetAttributeId(doc);
	}

	private boolean isPolicyReference(AbstractPolicy ap) {
		if (ap instanceof IdReference) {
			return ((IdReference) ap).isPolicyReference();
		}
		return false;
	}
	
	private boolean isPolicySetReference(AbstractPolicy ap) {
		if (ap instanceof IdReference) {
			return ((IdReference) ap).isPolicySetReference();
		}
		return false;
	}
}
