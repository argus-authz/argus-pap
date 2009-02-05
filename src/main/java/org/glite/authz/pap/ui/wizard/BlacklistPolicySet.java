package org.glite.authz.pap.ui.wizard;

import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;

public class BlacklistPolicySet extends PolicySetWizard {
    
    public static final String POLICY_SET_ID = "BlacklistPolicySet";

    public BlacklistPolicySet() {
        super("BlacklistPolicySet", PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS, null, null);
    }

}
