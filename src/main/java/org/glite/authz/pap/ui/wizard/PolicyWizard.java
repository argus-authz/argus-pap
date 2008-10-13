package org.glite.authz.pap.ui.wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.ui.wizard.AttributeWizard.AttributeWizardType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.TargetType;

public class PolicyWizard {

    private enum PolicyWizardType {
        BLACKLIST, SERVICECLASS
    }

    private static final String BLACKLIST_POLICY_ID_PREFIX = "BlacklistPolicy";
    private static final String SERVICECLASS_POLICY_ID_PREFIX = "ServiceClassPolicy";
    private static final String VISIBILITY_PRIVATE_PREFIX = "PRIVATE";
    private static final String VISIBILITY_PUBLIC_PREFIX = "PUBLIC";

    public static String generateId(String prefix) {
        return prefix + "_" + generateUUID();
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private static PolicyWizardType getPolicyWizardType(List<AttributeWizard> targetAttributeWizardList) {
        AttributeWizardType serviceClassWizardType = AttributeWizardType.SERVICE_CLASS;
        
        for (AttributeWizard attribute : targetAttributeWizardList) {
            
            if (serviceClassWizardType.equals(attribute.getAttributeWizardType()))
                return PolicyWizardType.SERVICECLASS;
        }
        
        return PolicyWizardType.BLACKLIST;
    }

    private boolean isPrivate = false;
    private final List<List<AttributeWizard>> orExceptionsAttributeWizardList;
    private final PolicyType policy;
    private String policyIdPrefix;
    private String policyIdUniqueNumber;
    private String policyIdVisibilityPrefix;
    private PolicyWizardType policyWizardType;
    private final List<AttributeWizard> targetAttributeWizardList;

    public PolicyWizard(PolicyType policy) throws UnsupportedPolicyException {
        
        decomposePolicyId(policy.getPolicyId());

        if (policy.getRules().size() != 1)
            throw new UnsupportedPolicyException("Wrong number of rules");

        targetAttributeWizardList = TargetWizard.extractTargetAttributeWizardList(policy.getTarget());
        
        if (targetAttributeWizardList.isEmpty())
            throw new UnsupportedPolicyException("ANY TARGET not supported");

        policyWizardType = getPolicyWizardType(targetAttributeWizardList);

        orExceptionsAttributeWizardList = RuleWizard.getAttributeWizardList(policy.getRules().get(0));

        this.policy = policy;
    }

    public PolicyWizard(List<AttributeWizard> targetAttributeWizardList,
            List<List<AttributeWizard>> orExceptionsAttributeWizardList, EffectType effect) {

        if (targetAttributeWizardList == null)
            targetAttributeWizardList = new LinkedList<AttributeWizard>();
        this.targetAttributeWizardList = targetAttributeWizardList;

        policyWizardType = getPolicyWizardType(targetAttributeWizardList);

        if (orExceptionsAttributeWizardList == null)
            orExceptionsAttributeWizardList = new LinkedList<List<AttributeWizard>>();
        this.orExceptionsAttributeWizardList = orExceptionsAttributeWizardList;

        policy = PolicyHelper.build(generateId(), PolicyHelper.RULE_COMBALG_DENY_OVERRIDS);

        TargetType target = TargetWizard.createTarget(targetAttributeWizardList);

        RuleType rule = RuleWizard.build(orExceptionsAttributeWizardList, effect);

        policy.setTarget(target);
        policy.getRules().add(rule);

    }

    public List<List<AttributeWizard>> getOrExceptionsAttributeWizardList() {
        return orExceptionsAttributeWizardList;
    }

    public String getPolicyIdPrefix() {
        return policyIdVisibilityPrefix + "_" + policyIdPrefix;
    }

    public PolicyType getPolicyType() {
        return policy;
    }

    public List<AttributeWizard> getTargetAttributeList() {
        return targetAttributeWizardList;
    }

    public boolean isBlacklistPolicy() {
    	
        if (policyWizardType.equals(PolicyWizardType.BLACKLIST))
            return true;
        
        return false;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
    
    public static boolean isPrivate(String policyId) {
        String[] idComponents = policyId.split("_");
        
        if (idComponents.length != 3)
            return false;
        
        if (VISIBILITY_PRIVATE_PREFIX.equals(idComponents[0]))
            return true;
        
        return false;
    }

    public boolean isServiceClassPolicy() {
    	
        if (policyWizardType.equals(PolicyWizardType.SERVICECLASS))
            return true;
        
        return false;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
        
        setPolicyIdVisibilityPrefix(isPrivate);
        
        policy.setPolicyId(composeId());
    }

    public String toFormattedString() {

        String formattedString = "id=" + policy.getPolicyId() + "\n";

        String identation;
        if (EffectType.Deny.equals(policy.getRules().get(0).getEffect())) {
            formattedString += "    deny ";
            identation = "         ";
        } else {
            formattedString += "    allow ";
            identation = "          ";
        }

        formattedString += targetAttributeWizardList.get(0).toFormattedString() + "\n";

        for (int i = 1; i < targetAttributeWizardList.size(); i++) {
            formattedString += identation + targetAttributeWizardList.get(i).toFormattedString() + "\n";
        }

        for (List<AttributeWizard> andList : orExceptionsAttributeWizardList) {
            if (andList.isEmpty())
                continue;

            formattedString += "    except " + andList.get(0).toFormattedString() + "\n";

            for (int i = 1; i < andList.size(); i++) {
                formattedString += "           " + andList.get(i).toFormattedString() + "\n";
            }
        }

        return formattedString;
    }
    
    public String toNormalizedFormattedString(int indent) {
    	int firstPadding = 4;
    	
    	String firstLevelIndentString = fillwithSpaces(indent);

        String formattedString = firstLevelIndentString + "id=" + policy.getPolicyId() + "\n";
        
        String secondLevelIndent = fillwithSpaces(indent + firstPadding);

        String effectString;
        
        if (EffectType.Deny.equals(policy.getRules().get(0).getEffect()))
        	effectString = "deny ";
        else
            effectString = "allow ";

        String thirdLevelIndent = fillwithSpaces(indent + firstPadding + effectString.length());
        formattedString += secondLevelIndent + effectString;

        for (int i = 0; i < targetAttributeWizardList.size(); i++) {
        	
        	AttributeWizard attributeWizard = targetAttributeWizardList.get(i);
        	
        	if ((!AttributeWizardType.RESOURCE_URI.equals(attributeWizard.getAttributeWizardType())
        			&& (!AttributeWizardType.SERVICE_CLASS.equals(attributeWizard)))) {

        		if (i > 0)
            		formattedString += thirdLevelIndent;
        		
        		formattedString += targetAttributeWizardList.get(i).toFormattedString() + "\n";
        		
        	}
        }
        
        thirdLevelIndent = fillwithSpaces(indent + firstPadding + "except".length() + 1);

        for (List<AttributeWizard> andList : orExceptionsAttributeWizardList) {
            if (andList.isEmpty())
                continue;

            formattedString += secondLevelIndent + "except " + andList.get(0).toFormattedString() + "\n";

            for (int i = 1; i < andList.size(); i++) {
                formattedString += thirdLevelIndent + andList.get(i).toFormattedString() + "\n";
            }
        }

        return formattedString;
    }

    public String toString() {
        return PolicyHelper.toString(policy);
    }

    private String generateId() {
        
        setPolicyIdVisibilityPrefix(isPrivate);

        setPolicyIdPrefix(policyWizardType);
        
        policyIdUniqueNumber = generateUUID();

        return composeId();
    }
    
    private void setPolicyIdVisibilityPrefix(boolean isPrivate) {
        if (isPrivate)
            policyIdVisibilityPrefix = VISIBILITY_PRIVATE_PREFIX;
        else
            policyIdVisibilityPrefix = VISIBILITY_PUBLIC_PREFIX;
    }
    
    private void setPolicyIdPrefix(PolicyWizardType wizardType) {
        if (wizardType.equals(PolicyWizardType.BLACKLIST))
            policyIdPrefix = BLACKLIST_POLICY_ID_PREFIX;
        else
            policyIdPrefix = SERVICECLASS_POLICY_ID_PREFIX;
    }
    
    private String composeId() {
        return policyIdVisibilityPrefix + "_" + policyIdPrefix + "_" + policyIdUniqueNumber;
    }
    
    private void decomposePolicyId(String policyId) throws UnsupportedPolicyException {
        
        String[] idComponents = policyId.split("_");
        
        if (idComponents.length != 3)
            throw new UnsupportedPolicyException("Unrecognized policyId: " + policyId);
        
        
        if (VISIBILITY_PRIVATE_PREFIX.equals(idComponents[0])) {
            this.isPrivate = true;
            setPolicyIdVisibilityPrefix(true);
        } else {
            this.isPrivate = false;
            setPolicyIdVisibilityPrefix(false);
        }
        
        if (BLACKLIST_POLICY_ID_PREFIX.equals(idComponents[1]))
            setPolicyIdPrefix(PolicyWizardType.BLACKLIST);
        else
            setPolicyIdPrefix(PolicyWizardType.SERVICECLASS);
        
        policyIdUniqueNumber = idComponents[2];
                
    }
    
    private String fillwithSpaces(int n) {
    	String s = "";
    	
    	for (int i=0; i<n; i++)
    		s += " ";
    	
    	return s;
    }

}
