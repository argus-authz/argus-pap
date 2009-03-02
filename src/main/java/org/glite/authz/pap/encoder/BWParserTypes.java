package org.glite.authz.pap.encoder;

import java.lang.String;
import org.glite.authz.pap.common.xacml.wizard.RuleWizard;
import org.glite.authz.pap.common.xacml.wizard.ObligationWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;

class MixIn {
    public RuleWizard rule;
    public ObligationWizard obligation;
    public String description;
    public PolicyWizard policy;

    public MixIn(RuleWizard r) {
        rule = r;
    }

    public MixIn(ObligationWizard o) {
        obligation = o;
    }

    public MixIn(String s) {
        description = s;
    }

    public MixIn(PolicyWizard pw) {
        policy = pw;
    }
};

