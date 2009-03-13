package org.glite.authz.pap.common.xacml.utils;

import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;

public class ObligationHelper extends XMLObjectHelper<ObligationType> {

    private static final javax.xml.namespace.QName elementQName = ObligationType.DEFAULT_ELEMENT_QNAME;
    private static final ObligationHelper instance = new ObligationHelper();

    private ObligationHelper() {}

    public static ObligationType build(String obligationId, EffectType effect) {
        ObligationType obligation = (ObligationType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        obligation.setObligationId(obligationId);
        obligation.setFulfillOn(effect);
        return obligation;
    }
    
    public static void addAttributeAssignment(ObligationType obligation, String attributeId, String value, String dataType) {
        AttributeAssignmentType attributeAssignment = AttributeAssignmentHelper.build(attributeId, value, dataType);
        obligation.getAttributeAssignments().add(attributeAssignment);
    }

    public static ObligationHelper getInstance() {
        return instance;
    }

}
