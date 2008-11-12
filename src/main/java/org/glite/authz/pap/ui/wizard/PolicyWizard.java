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
    
    private static final String ALLOW_KEYWORD = "allow";
    private static final String BLACKLIST_POLICY_ID_PREFIX = "BlacklistPolicy";
    private static final String DENY_KEYWORD = "deny";
    private static final String EXCEPT_KEYWORD = "    except";
    private static final String SERVICECLASS_POLICY_ID_PREFIX = "ServiceClassPolicy";
    private static final String VISIBILITY_PRIVATE_PREFIX = "PRIVATE";
    private static final String VISIBILITY_PUBLIC_PREFIX = "PUBLIC";
    
    public static String generateId(String prefix) {
        return prefix + "_" + generateUUID();
    }
    
    public static boolean isPrivate(String policyId) {
        String[] idComponents = policyId.split("_");
        
        if (idComponents.length != 3)
            return false;
        
        if (VISIBILITY_PRIVATE_PREFIX.equals(idComponents[0]))
            return true;
        
        return false;
    }
    
    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }
    
    private static PolicyWizardType getPolicyWizardType(
            List<AttributeWizard> targetAttributeWizardList) {
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
    
    public PolicyWizard(PolicyType policy) throws UnsupportedPolicyException {
        
        decomposePolicyId(policy.getPolicyId());
        
        if (policy.getRules().size() != 1)
            throw new UnsupportedPolicyException("Wrong number of rules");
        
        targetAttributeWizardList = TargetWizard.extractTargetAttributeWizardList(policy
                .getTarget());
        
        if (targetAttributeWizardList.isEmpty())
            throw new UnsupportedPolicyException("ANY TARGET not supported");
        
        policyWizardType = getPolicyWizardType(targetAttributeWizardList);
        
        orExceptionsAttributeWizardList = RuleWizard.getAttributeWizardList(policy.getRules()
                .get(0));
        
        this.policy = policy;
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
    
    public boolean isBanPolicy(String attributeValue, AttributeWizardType bannedAttribute) {
        
        if (isServiceClassPolicy())
            return false;
        
        if (targetAttributeWizardList.size() != 2)
            return false;
        
        if (orExceptionsAttributeWizardList.size() != 0)
        	return false;
        
        for (AttributeWizard attribute:targetAttributeWizardList) {
            
            if (AttributeWizardType.RESOURCE_URI.equals(attribute.getAttributeWizardType())) {
                if (!("*".equals(attribute.getValue())))
                    return false;
            } else if (bannedAttribute.equals(attribute.getAttributeWizardType())) {
                if (!(attribute.getValue().equals(attributeValue)))
                    return false;
            } else
                return false;
        }
        
        return true;
    }
    
    public boolean isBanPolicyForDN(String dn) {
        return isBanPolicy(dn, AttributeWizardType.DN);
    }
    
    public boolean isBanPolicyForFQAN(String dn) {
        return isBanPolicy(dn, AttributeWizardType.FQAN);
    }
    
    
    public boolean isBlacklistPolicy() {
        
        if (policyWizardType.equals(PolicyWizardType.BLACKLIST))
            return true;
        
        return false;
    }
    
    public boolean isPrivate() {
        return isPrivate;
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
        return toFormattedString(0, 4);
    }
    
    public String toFormattedString(int policyIndent, int attributeIndent) {
        
        String policyIndentString = fillwithSpaces(policyIndent);
        
        String formattedString = policyIndentString + "id=" + policy.getPolicyId() + "\n";
        
        String effectIndentString = fillwithSpaces(policyIndent + attributeIndent);
        
        if (isPrivate())
            formattedString += effectIndentString + "private\n";
        
        String effectString;
        
        if (EffectType.Deny.equals(policy.getRules().get(0).getEffect()))
            effectString = DENY_KEYWORD + " ";
        else
            effectString = ALLOW_KEYWORD + " ";
        
        if (EffectType.Deny.equals(policy.getRules().get(0).getEffect()))
            effectString = DENY_KEYWORD + " ";
        else
            effectString = ALLOW_KEYWORD + " ";
        
        String attributeIndentString = fillwithSpaces(policyIndent + attributeIndent
                + effectString.length());
        formattedString += effectIndentString + effectString;
        
        for (int i = 0; i < targetAttributeWizardList.size(); i++) {
            
            if (i > 0)
                formattedString += attributeIndentString;
            
            formattedString += targetAttributeWizardList.get(i).toFormattedString() + "\n";
        }
        
        String exceptKeyString = EXCEPT_KEYWORD + " ";
        attributeIndentString = fillwithSpaces(policyIndent + attributeIndent
                + exceptKeyString.length());
        
        for (List<AttributeWizard> andList : orExceptionsAttributeWizardList) {
            if (andList.isEmpty())
                continue;
            
            formattedString += effectIndentString + exceptKeyString
                    + andList.get(0).toFormattedString() + "\n";
            
            for (int i = 1; i < andList.size(); i++) {
                formattedString += attributeIndentString + andList.get(i).toFormattedString()
                        + "\n";
            }
        }
        
        return formattedString;
    }
    
    public String toNormalizedFormattedString(int policyIndent, boolean noId) {
        return toNormalizedFormattedString(policyIndent, 4, noId);
    }
    
    public String toNormalizedFormattedString(int policyIndent, int attributeIndent, boolean noId) {
        
        String policyIndentString = fillwithSpaces(policyIndent);
        
        String formattedString;
        
        if (!noId)
            formattedString = policyIndentString + "id=" + policy.getPolicyId() + "\n";
        else
            formattedString = new String();
        
        String effectIndentString = fillwithSpaces(policyIndent + attributeIndent);
        
        if (isPrivate())
            formattedString += effectIndentString + "private\n";
        
        String effectString;
        
        if (EffectType.Deny.equals(policy.getRules().get(0).getEffect()))
            effectString = DENY_KEYWORD + " ";
        else
            effectString = ALLOW_KEYWORD + " ";
        
        String attributeIndentString = fillwithSpaces(policyIndent + attributeIndent
                + effectString.length());
        formattedString += effectIndentString + effectString;
        
        for (int i = 0; i < targetAttributeWizardList.size(); i++) {
            
            AttributeWizard attributeWizard = targetAttributeWizardList.get(i);
            AttributeWizardType awt = attributeWizard.getAttributeWizardType();
            
            if (AttributeWizardType.RESOURCE_URI.equals(awt)
                    || AttributeWizardType.SERVICE_CLASS.equals(awt))
                continue;
            
            if (i > 0)
                formattedString += attributeIndentString;
            
            formattedString += targetAttributeWizardList.get(i).toFormattedString() + "\n";
            
        }
        
        String exceptKeyString = EXCEPT_KEYWORD + " ";
        attributeIndentString = fillwithSpaces(policyIndent + attributeIndent
                + exceptKeyString.length());
        
        for (List<AttributeWizard> andList : orExceptionsAttributeWizardList) {
            if (andList.isEmpty())
                continue;
            
            formattedString += effectIndentString + exceptKeyString
                    + andList.get(0).toFormattedString() + "\n";
            
            for (int i = 1; i < andList.size(); i++) {
                formattedString += attributeIndentString + andList.get(i).toFormattedString()
                        + "\n";
            }
        }
        
        return formattedString;
    }
    
    public String toString() {
        return PolicyHelper.toString(policy);
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
        
        for (int i = 0; i < n; i++)
            s += " ";
        
        return s;
    }
    
    private String generateId() {
        
        setPolicyIdVisibilityPrefix(isPrivate);
        
        setPolicyIdPrefix(policyWizardType);
        
        policyIdUniqueNumber = generateUUID();
        
        return composeId();
    }
    
    private void setPolicyIdPrefix(PolicyWizardType wizardType) {
        if (wizardType.equals(PolicyWizardType.BLACKLIST))
            policyIdPrefix = BLACKLIST_POLICY_ID_PREFIX;
        else
            policyIdPrefix = SERVICECLASS_POLICY_ID_PREFIX;
    }
    
    private void setPolicyIdVisibilityPrefix(boolean isPrivate) {
        if (isPrivate)
            policyIdVisibilityPrefix = VISIBILITY_PRIVATE_PREFIX;
        else
            policyIdVisibilityPrefix = VISIBILITY_PUBLIC_PREFIX;
    }
    
}
