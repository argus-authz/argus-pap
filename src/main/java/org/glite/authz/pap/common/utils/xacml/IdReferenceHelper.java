package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.IdReferenceType;

public class IdReferenceHelper extends XMLObjectHelper<IdReferenceType> {

    public static enum Type {
        POLICY_ID_REFERENCE, POLICYSET_ID_REFERENCE;
    }

    private static final javax.xml.namespace.QName policyIdReferenceQName = IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME;
    private static final javax.xml.namespace.QName policyIdSetReferenceQName = IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME;
    private static IdReferenceHelper instance = new IdReferenceHelper();

    public static IdReferenceType build(Type type, String value) {
        IdReferenceType idReference;
        if (type == Type.POLICYSET_ID_REFERENCE) {
            idReference = (IdReferenceType) builderFactory.getBuilder(policyIdSetReferenceQName)
                    .buildObject(policyIdSetReferenceQName);
        } else {
            idReference = (IdReferenceType) builderFactory.getBuilder(policyIdReferenceQName)
                    .buildObject(policyIdReferenceQName);
        }
        idReference.setValue(value);
        return idReference;
    }

    public static IdReferenceHelper getInstance() {
        return instance;
    }

    public static boolean isPolicyIdReference(IdReferenceType idReference) {
        if (idReference.getElementQName().equals(policyIdReferenceQName)) {
            return true;
        }
        return false;
    }

    public static boolean isPolicySetIdReference(IdReferenceType idReference) {
        if (idReference.getElementQName().equals(policyIdSetReferenceQName)) {
            return true;
        }
        return false;
    }

    private IdReferenceHelper() {}

}
