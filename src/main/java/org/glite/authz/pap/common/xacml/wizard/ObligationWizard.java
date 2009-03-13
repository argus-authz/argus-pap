package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.utils.ObligationHelper;
import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;

public class ObligationWizard {

    private List<AttributeWizard> attributeWizardList;
    private ObligationType obligation = null;
    private String obligationId = null;

    public ObligationWizard(String obligationId) {
        this(obligationId, new LinkedList<AttributeWizard>());
    }

    public ObligationWizard(String obligationId, List<AttributeWizard> attributeWizardList) {
        
        if (attributeWizardList == null) {
            this.attributeWizardList = new LinkedList<AttributeWizard>();
        } else {
            this.attributeWizardList = attributeWizardList;
        }
        
        this.obligationId = obligationId;
    }
    
    public ObligationWizard(ObligationType obligation) {
        obligationId = obligation.getObligationId();
        attributeWizardList = new LinkedList<AttributeWizard>();
        
        for (AttributeAssignmentType attributeAssignment : obligation.getAttributeAssignments()) {
            attributeWizardList.add(new AttributeWizard(attributeAssignment));
        }
    }
    
    public void addAttributeAssignment(AttributeWizard attributeWizard) {
        invalidateObligationType();
        attributeWizardList.add(attributeWizard);
    }
    
    public String getTagAndValue() {
        return String.format("obliagtion \"%s\"", obligation, obligationId);
    }
    
    public ObligationType getXACML() {
        initObligationTypeIfNotSet();
        return obligation;
    }

    public void releaseChildrenDOM() {
        releaseDOM();
    }
    
    public void releaseDOM() {
        if (obligation != null) {
            obligation.releaseChildrenDOM(true);
            obligation.releaseDOM();
        }
    }
    
    public String toFormattedString() {
        return toFormattedString(0, 4);
    }

    public String toFormattedString(int baseIndentation, int internalIndentation) {

        String baseIndentString = Utils.fillWithSpaces(baseIndentation);
        String indentString = Utils.fillWithSpaces(baseIndentation + internalIndentation);
        StringBuffer sb = new StringBuffer();

        sb.append(String.format("%sobligation \"%s\" {\n", baseIndentString, obligationId));
        
        for (AttributeWizard attributeWizard : attributeWizardList) {
            sb.append(indentString + attributeWizard.toFormattedString() + "\n");
        }
        
        sb.append(baseIndentString + "}");
        
        return sb.toString();
    }

    private void initObligationTypeIfNotSet() {
       if (obligation == null) {
           setObligationType();
       }
    }
    
    private void invalidateObligationType() {
        obligation.releaseChildrenDOM(true);
        obligation.releaseDOM();
        obligation = null;
    }
    
    private void setObligationType() {
        releaseDOM();

        obligation = ObligationHelper.build(obligationId, EffectType.Permit);

        for (AttributeWizard attributeWizard : attributeWizardList) {
            ObligationHelper.addAttributeAssignment(obligation,
                                                    attributeWizard.getXacmlId(),
                                                    attributeWizard.getValue(),
                                                    attributeWizard.getDataType());
        }
    }
}
