package org.glite.authz.pap.encoder;

import java.util.Vector;
import java.util.Enumeration;
import java.lang.String;
import org.glite.authz.pap.ui.wizard.*;

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
            policySet = BlackListPolicySet.build();


        Enumeration conditions = conds.fullconditions.elements();

        while (conditions.hasMoreElements()) {
            FullCondition fc = (FullCondition)conditions.nextElement();
            PolicyType policy = OutputFC(fc, previousType);

            PolicySetHelper.addPolicyReference(policySet, policy.getPolicyId());
        }

        return policySet
    }

    private PolicySetType OutputSClass(int previousType) {
        return policies.Output(POLICY_TYPE_CLASS);
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

        List<List<AttributeWizard>> exceptList = new LinkedList<List<AttributeWizard>>();
        if (excepts != null) {
            Enumeration exceptlist = excepts.rows.elements();

            while (exceptlist.hasMoreElements()) {
                Enumeration singlesexcepts = ((ConditionRow)(exceptlist.nextElement())).singles.elements();
                List<AttributeWizard> exceptRowList = new LinkedList<AttributeWizard>();
                while (singlesexcepts.hasMoreElements()) {
                    exceptRowList.add(OutputCondition((SingleCondition) singlesexcepts.nextElement()));
                }
                exceptsList.add(exceptRowList);
            }
        }

        PolicyType policy = null;

        switch (previousType) {
        case POLICY_TYPE_BW:
            policy = new BlacklistPolicy.build(targetAttributeList, exceptList);
            break;

        case POLICY_TYPE_SCLASS:
            policy = new ServiceClassPolicy(targetAttributeList, exceptList, (fc.allow ? "Permit" : "Deny"));
            break;

        case POLICY_TYPE_UNKNOWN:
            Random gen = new Random();
            String id = "policy_" + gen.nextLong();
            policy = new PolicyWizard.build(id, targetAttributeList, exceptList,
                                            (fc.allow ? "Permit" : "Deny"));
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
    Vector policies;

    public Policies() {
        policies = new Vector();
    }

    public List<XACMLObject> Output() {
        String XACML = new String();
        Enumeration i = policies.elements();

        List <XACMLObject> resultList = new LinkedList<XACMLObject>();

        PolicySetType localPAPPolicySet = LocalPAPPolicySet.build();

        while (i.hasMoreElements()) {
            localPAPPolicySet.add(((Policy)i.nextElement()).Output());
        }
        resultList.add(localPAPPolicySet);

        return resultList;
    }
};

