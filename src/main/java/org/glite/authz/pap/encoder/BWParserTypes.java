package org.glite.authz.pap.encoder;

import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.XACMLObject;
import java.util.Vector;
import java.util.Enumeration;
import java.lang.String;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.*;

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
    public static final int TYPE_RESOURCE = 4;
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
    String serviceClass;
    int type;
    Conds conds;
    Policies policies;
    public static final int POLICY_TYPE_BW      = 0;
    public static final int POLICY_TYPE_CLASS   = 1;
    public static final int POLICY_TYPE_UNKNOWN = -1;

    public Policy() {
        type = POLICY_TYPE_BW;
        serviceClass = null;
    }

    public void Output(BlacklistPolicySet blSet, 
                       ServiceClassPolicySet clSet,
                       int previousType) {
        if (type == POLICY_TYPE_BW)
            OutputBW(blSet, clSet, previousType);
        else
            OutputSClass(blSet, clSet, previousType);
    }

    private void OutputBW(BlacklistPolicySet blSet,
                          ServiceClassPolicySet clSet,
                          int previousType) {

        Enumeration conditions = conds.fullconditions.elements();

        while (conditions.hasMoreElements()) {
            FullCondition fc = (FullCondition)conditions.nextElement();
            PolicyWizard policy = OutputFC(fc, previousType);

            if (previousType == POLICY_TYPE_CLASS)
                clSet.addPolicy(policy);
            else
                blSet.addPolicy(policy);
        }
    }

    private void OutputSClass(BlacklistPolicySet blSet,
                              ServiceClassPolicySet scSet,
                              int previousType) {
        Vector policiesList = policies.policies;

        Enumeration i = policiesList.elements();

        while (i.hasMoreElements()) {
            Policy p = (Policy)i.nextElement();
            p.serviceClass = name;
            p.OutputBW(blSet, scSet, POLICY_TYPE_CLASS);
        }
    }

    private PolicyWizard OutputFC(FullCondition fc, int previousType) {
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

        PolicyWizard policy = null;

        switch (previousType) {
        case POLICY_TYPE_BW:
            policy = new BlacklistPolicy(targetAttributeList, exceptsAttributes);
            break;

        case POLICY_TYPE_CLASS:
            targetAttributeList.add(new AttributeWizard("service_class", serviceClass));
            policy = new ServiceClassPolicy(targetAttributeList, exceptsAttributes, (fc.allow ? org.opensaml.xacml.policy.EffectType.Permit : org.opensaml.xacml.policy.EffectType.Deny));
            break;

        case POLICY_TYPE_UNKNOWN:
            break;
        }

        policy.setPrivate(fc.isprivate);
            
        return policy;
    }

    private AttributeWizard OutputCondition(SingleCondition sc) {
        switch (sc.type) {

        case SingleCondition.TYPE_FQAN:
            return new AttributeWizard("fqan", sc.value1);

        case SingleCondition.TYPE_DN:
            return new AttributeWizard("dn", sc.value1);

        case SingleCondition.TYPE_GA:
            return new AttributeWizard(sc.value1, sc.value2);

        case SingleCondition.TYPE_CERT:
            return new AttributeWizard("cert", sc.value1 + ":" + sc.value2);

        case SingleCondition.TYPE_RESOURCE:
            return new AttributeWizard("resource_uri", sc.value1);
        }
        return null;
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

        PolicySetWizard localPAPPolicySet = new LocalPAPPolicySet();
        BlacklistPolicySet blSet = new BlacklistPolicySet();
        ServiceClassPolicySet scSet = new ServiceClassPolicySet();

        while (i.hasMoreElements()) {
            ((Policy)i.nextElement()).Output(blSet, scSet, Policy.POLICY_TYPE_BW);
        }

        localPAPPolicySet.addPolicySet(blSet);
        localPAPPolicySet.addPolicySet(scSet);

        return localPAPPolicySet.getPolicyTreeAsList();
    }
};
