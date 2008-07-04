package org.glite.authz.pap.encoder;

import java.util.Vector;
import java.util.Enumeration;
import java.lang.String;
import org.glite.authz.pap.ui.wizard.*;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.XACMLObject;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;




class SingleCondition {
    int type;
    String value1;
    String value2;
    boolean yesorno;
    public static final int TYPE_FQAN     = 0;
    public static final int TYPE_DN       = 1;
    public static final int TYPE_GA       = 2;
    public static final int TYPE_CERT     = 3;
    public static final int TYPE_PILOT    = 4;
    public static final int TYPE_RESOURCE = 5;
};

class ConditionRow {
    Vector singles;
    public ConditionRow() {
        singles = new Vector();
    }
};

class ConditionList {
    Vector rows;

    public ConditionList() {
        rows = new Vector();
    }
};

class FullCondition {
    boolean allow;
    boolean isprivate;
    ConditionRow simples;
    ConditionList excepts;

    public FullCondition() {
        simples = new ConditionRow();
        excepts = new ConditionList();
    }
};

class Conds {
    Vector fullconditions;

    public Conds() {
        fullconditions = new Vector();
    }
};

class Policy {
    String name;
    int type;
    Conds conds;
    Policies policies;
    public static final int POLICY_TYPE_BW      = 0;
    public static final int POLICY_TYPE_CLASS   = 1;
    public static final int POLICY_TYPE_UNKNOWN = -1;

    public Policy() {
        type = POLICY_TYPE_BW;
    }

    public PolicySetType Output(int previousType) {
        if (type == POLICY_TYPE_BW)
            return OutputBW(previousType);
        else
            return OutputSClass(previousType);
    }

    private PolicySetType OutputBW(int previousType) {
        PolicySetType policySet = null;

        if (previousType == POLICY_TYPE_CLASS)
            policySet = ServiceClassPolicySet.build();
        else
            policySet = BlacklistPolicySet.build();


        Enumeration conditions = conds.fullconditions.elements();

        while (conditions.hasMoreElements()) {
            FullCondition fc = (FullCondition)conditions.nextElement();
            PolicyType policy = OutputFC(fc, previousType);

            PolicySetHelper.addPolicyReference(policySet, policy.getPolicyId());
        }

        return policySet;
    }

    private PolicySetType OutputSClass(int previousType) {
        PolicySetType policySet = ServiceClassPolicySet.build();

        Vector policiesList = policies.policies;

        Enumeration i = policiesList.elements();

        while (i.hasMoreElements()) {
            PolicySetHelper.addPolicySetReference(policySet, (((Policy)i.nextElement()).Output(previousType)).getPolicySetId());
        }

        return policySet;
    }

    private PolicyType OutputFC(FullCondition fc, int previousType) {
        ConditionRow denys = fc.simples;
        ConditionList excepts = fc.excepts;

        List<AttributeWizard> targetAttributeList = new LinkedList<AttributeWizard>();
        targetAttributeList.add(new AttributeWizard("resource_uri", name));

        Enumeration singles = denys.singles.elements();
        while (singles.hasMoreElements()) {
            targetAttributeList.add(OutputCondition((SingleCondition) singles.nextElement()));
        }

        List<List<AttributeWizard>> exceptsAttributes = new LinkedList<List<AttributeWizard>>();
        if (excepts != null) {
            Enumeration exceptList = excepts.rows.elements();

            while (exceptList.hasMoreElements()) {
                Enumeration singlesexcepts = ((ConditionRow)(exceptList.nextElement())).singles.elements();
                List<AttributeWizard> exceptRowList = new LinkedList<AttributeWizard>();
                while (singlesexcepts.hasMoreElements()) {
                    exceptRowList.add(OutputCondition((SingleCondition) singlesexcepts.nextElement()));
                }
                exceptsAttributes.add(exceptRowList);
            }
        }

        PolicyType policy = null;

        switch (previousType) {
        case POLICY_TYPE_BW:
            policy = BlacklistPolicy.build(targetAttributeList, exceptsAttributes);
            break;

        case POLICY_TYPE_CLASS:
            policy = ServiceClassPolicy.build(targetAttributeList, exceptsAttributes, (fc.allow ? org.opensaml.xacml.policy.EffectType.Permit : org.opensaml.xacml.policy.EffectType.Deny));
            break;

        case POLICY_TYPE_UNKNOWN:
            break;
        }

        return policy;
    }

    private AttributeWizard OutputCondition(SingleCondition sc) {
        switch (sc.type) {

        case SingleCondition.TYPE_FQAN:
            return new AttributeWizard("fqan", sc.value1);
            break;

        case SingleCondition.TYPE_DN:
            return new AttributeWizard("dn", sc.value1);
            break;

        case SingleCondition.TYPE_GA:
            return new AttributeWizard(sc.value1, sc.value2);
            break;

        case SingleCondition.TYPE_CERT:
            return new AttributeWizard("cert", sc.value1 + ":" + sc.value2);
            break;

        case SingleCondition.TYPE_PILOT:
            return new AttributeWizard("pilot", (sc.yesorno ? "Yes" : "No"));
            break;

        case SingleCondition.TYPE_RESOURCE:
            return new AttributeWizard("resource_uri", sc.value1);
            break;
        }
    }
};

class Policies {
    public Vector policies;

    public Policies() {
        policies = new Vector();
    }

    public List<XACMLObject> Output() {
        Enumeration i = policies.elements();

        List <XACMLObject> resultList = new LinkedList<XACMLObject>();

        PolicySetType localPAPPolicySet = LocalPAPPolicySet.build();

        while (i.hasMoreElements()) {
            PolicySetHelper.addPolicySetReference(localPAPPolicySet, (((Policy)i.nextElement()).Output(Policy.POLICY_TYPE_BW)).getPolicySetId());
        }
        resultList.add(localPAPPolicySet);

        return resultList;
    }
};
