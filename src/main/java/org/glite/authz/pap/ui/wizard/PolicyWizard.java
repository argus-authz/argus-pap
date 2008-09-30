package org.glite.authz.pap.ui.wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.glite.authz.pap.common.utils.xacml.ActionsHelper;
import org.glite.authz.pap.common.utils.xacml.Functions;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.ResourceHelper;
import org.glite.authz.pap.common.utils.xacml.ResourceMatchHelper;
import org.glite.authz.pap.common.utils.xacml.ResourcesHelper;
import org.glite.authz.pap.common.utils.xacml.SubjectHelper;
import org.glite.authz.pap.common.utils.xacml.SubjectMatchHelper;
import org.glite.authz.pap.common.utils.xacml.SubjectsHelper;
import org.glite.authz.pap.common.utils.xacml.TargetHelper;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;

public class PolicyWizard {
    
    private enum PolicyWizardType {
        BLACKLIST, SERVICECLASS
    }
    private static final String BLACKLIST_POLICY_ID_PREFIX = "BlacklistPolicy_";
    
    private static final String SERVICECLASS_POLICY_ID_PREFIX = "ServiceClassPolicy_";
    
    public static String generateId(String prefix) {
        return prefix + "_" + generateUUID();
    }
    
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
    
    private static TargetType createTarget(List<AttributeType> sbjAttr, List<AttributeType> rsrcAttr,
            List<AttributeType> envAttr) {

        SubjectsType subjects = SubjectsHelper.build(SubjectHelper.build(SubjectMatchHelper
                .buildListWithDesignator(sbjAttr, Functions.STRING_EQUAL)));

        ActionsType actions = ActionsHelper.buildAnyAction();

        ResourcesType resources = ResourcesHelper.build(ResourceHelper.build(ResourceMatchHelper
                .buildWithDesignator(rsrcAttr, Functions.STRING_EQUAL)));

        TargetType target = TargetHelper.build(subjects, actions, resources, null);

        return target;
    }

    private static List<AttributeWizard> extractTargetAttributeWizardList(TargetType target) {
        
        List<AttributeWizard> attributeWizardList = new LinkedList<AttributeWizard>();
        List<AttributeType> attributeList = new LinkedList<AttributeType>();

        List<SubjectType> subjectList = target.getSubjects().getSubjects();
        
        if (!subjectList.isEmpty()) {
        
            if (subjectList.size() > 1)
                throw new UnsupportedPolicyException("Policy has more than one Subject");
            
            attributeList.addAll(SubjectMatchHelper.getAttributeList(subjectList.get(0).getSubjectMatches()));
        }
        
        List<ResourceType> resourceList = target.getResources().getResources();
        
        if (!resourceList.isEmpty()) {
        
            if (resourceList.size() > 1)
                throw new UnsupportedPolicyException("Policy has more than one Resource");
            
            attributeList.addAll(ResourceMatchHelper.getAttributeList(resourceList.get(0).getResourceMatches()));
        }
        
        for (AttributeType attribute:attributeList) {
            attributeWizardList.add(new AttributeWizard(attribute));
        }
        
        return attributeWizardList;
    }

    private static List<AttributeType> getAttributeTypeList(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            resultList.add(attribute.getAttributeType());
        }

        return resultList;
    }

    private static List<List<AttributeType>> getAttributeTypeListList(List<List<AttributeWizard>> listList) {
        List<List<AttributeType>> resultList = new LinkedList<List<AttributeType>>();

        for (List<AttributeWizard> list : listList) {
            resultList.add(getAttributeTypeList(list));
        }

        return resultList;
    }

    private static List<AttributeType> getEnvironmentAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isEnvironmentAttribute())
                resultList.add(attribute.getAttributeType());
        }

        return resultList;
    }

    private static PolicyWizardType getPolicyWizardType(List<AttributeWizard> targetAttributeWizardList) {
        PolicyWizardType policyWizardType = PolicyWizardType.BLACKLIST;
        for (AttributeWizard attribute:targetAttributeWizardList) {
            if (AttributeWizard.AttributeWizardType.SERVICE_CLASS.equals(attribute.getAttributeWizardType())) {
                policyWizardType = PolicyWizardType.SERVICECLASS;
            }
        }
        return policyWizardType;
    }
    private static List<AttributeType> getResourceAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isResourceAttribute())
                resultList.add(attribute.getAttributeType());
        }

        return resultList;
    }
    private static List<AttributeType> getSubjectAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isSubjectAttribute())
                resultList.add(attribute.getAttributeType());
        }

        return resultList;
    }
    private final List<AttributeWizard> targetAttributeWizardList;

    private final List<List<AttributeWizard>> orExceptionsAttributeWizardList;
    
    private PolicyWizardType policyWizardType;
    
    protected final PolicyType policy;
    
    public PolicyWizard(List<AttributeWizard> targetAttributeWizardList,
            List<List<AttributeWizard>> orExceptionsAttributeWizardList, EffectType effect) {
        this(null, targetAttributeWizardList, orExceptionsAttributeWizardList, effect);
    }
    
    public PolicyWizard(PolicyType policy) throws UnsupportedPolicyException {
        
        if (policy.getRules().size() != 1)
            throw new UnsupportedPolicyException("Wrong number of rules");
        
        this.targetAttributeWizardList = extractTargetAttributeWizardList(policy.getTarget());
        if (targetAttributeWizardList.isEmpty())
            throw new UnsupportedPolicyException("ANY TARGET not supported");
        
        policyWizardType = getPolicyWizardType(targetAttributeWizardList);
        
        this.orExceptionsAttributeWizardList = ExceptionsRule.getAttributeWizardList(policy.getRules().get(0));
        
        this.policy = policy;
    }

    public PolicyWizard(String policyId, List<AttributeWizard> targetAttributeWizardList,
            List<List<AttributeWizard>> orExceptionsAttributeWizardList, EffectType effect) {

        if (targetAttributeWizardList == null)
            targetAttributeWizardList = new LinkedList<AttributeWizard>();
        this.targetAttributeWizardList = targetAttributeWizardList;
        
        policyWizardType = getPolicyWizardType(targetAttributeWizardList);

        if (orExceptionsAttributeWizardList == null)
            orExceptionsAttributeWizardList = new LinkedList<List<AttributeWizard>>();
        this.orExceptionsAttributeWizardList = orExceptionsAttributeWizardList;

        if (policyId == null)
            policyId = generateId();
        
        policy = PolicyHelper.build(policyId, PolicyHelper.RULE_COMBALG_DENY_OVERRIDS);

        TargetType target = createTarget(getSubjectAttributes(targetAttributeWizardList),
                getResourceAttributes(targetAttributeWizardList),
                getEnvironmentAttributes(targetAttributeWizardList));

        RuleType rule = ExceptionsRule.build(getAttributeTypeListList(orExceptionsAttributeWizardList),
                effect);

        policy.setTarget(target);
        policy.getRules().add(rule);

    }

    public List<List<AttributeWizard>> getOrExceptionsAttributeWizardList() {
        return orExceptionsAttributeWizardList;
    }
    
    public String getPolicyIdPrefix() {
        if (isBlacklistPolicy())
            return BLACKLIST_POLICY_ID_PREFIX;
        else if (isServiceClassPolicy())
            return SERVICECLASS_POLICY_ID_PREFIX;
        return "";
    }
    
    public PolicyType getPolicyType() {
        return policy;
    }
    
    public List<AttributeWizard> getTargetAttributeList() {
        return targetAttributeWizardList;
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
        
        for (int i=1; i<targetAttributeWizardList.size(); i++) {
            formattedString += identation + targetAttributeWizardList.get(i).toFormattedString() + "\n";
        }
        
        for (List<AttributeWizard> andList:orExceptionsAttributeWizardList) {
            if (andList.isEmpty())
                continue;
            
            formattedString += "    except " + andList.get(0).toFormattedString() + "\n";
            
            for (int i=1; i<andList.size(); i++) {
                formattedString += "           " + andList.get(i).toFormattedString() + "\n";
            }
        }
        
        return formattedString;
    }
    
    public String toString() {
        return PolicyHelper.toString(policy);
    }
    
    public boolean isBlacklistPolicy() {
        if (policyWizardType.equals(PolicyWizardType.BLACKLIST))
            return true;
        return false;
    }
    
    public boolean isServiceClassPolicy() {
        if (policyWizardType.equals(PolicyWizardType.SERVICECLASS))
            return true;
        return false;
    }
    
    private String generateId() {
        String prefix;
        
        if (policyWizardType.equals(PolicyWizardType.BLACKLIST))
            prefix = BLACKLIST_POLICY_ID_PREFIX;
        else
            prefix = SERVICECLASS_POLICY_ID_PREFIX;
        
        return prefix + "_" + generateUUID();
    }

}
