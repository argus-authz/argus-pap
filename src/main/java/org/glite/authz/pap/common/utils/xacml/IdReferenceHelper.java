package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xml.Configuration;

public class IdReferenceHelper extends XACMLHelper<IdReferenceType> {

    public static enum Type {
	POLICY_ID_REFERENCE, POLICYSET_ID_REFERENCE;
    }

    private static IdReferenceHelper instance = null;

    public static IdReferenceHelper getInstance() {
	if (instance == null) {
	    instance = new IdReferenceHelper();
	}
	return instance;
    }

    private IdReferenceHelper() {
    }

    public static IdReferenceType build(Type type, String value) {
	IdReferenceType idReference;
	if (type == Type.POLICYSET_ID_REFERENCE) {
	    idReference = (IdReferenceType) Configuration
		    .getBuilderFactory()
		    .getBuilder(
			    IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME)
		    .buildObject(
			    IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME);
	} else {
	    idReference = (IdReferenceType) Configuration.getBuilderFactory()
		    .getBuilder(
			    IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME)
		    .buildObject(
			    IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME);
	}
	idReference.setValue(value);
	return idReference;
    }

    public static boolean isPolicyIdReference(IdReferenceType idReference) {
	if (idReference.getElementQName().equals(
		IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME)) {
	    return true;
	}
	return false;
    }

    public static boolean isPolicySetIdReference(IdReferenceType idReference) {
	if (idReference.getElementQName().equals(
		IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME)) {
	    return true;
	}
	return false;
    }

}
