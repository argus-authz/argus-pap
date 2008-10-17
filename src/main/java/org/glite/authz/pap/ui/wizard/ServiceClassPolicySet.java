package org.glite.authz.pap.ui.wizard;

import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;

public class ServiceClassPolicySet extends PolicySetWizard {
    
    public static final String POLICY_SET_ID = "ServiceClassPolicySet";

    public ServiceClassPolicySet() {
        super("ServiceClassPolicySet", PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS, null, null);
    }
    
}
