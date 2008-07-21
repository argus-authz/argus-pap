package org.glite.authz.pap.ui.wizard;

import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.opensaml.xacml.policy.PolicySetType;

public class ServiceClassPolicySet extends PolicySetWizard {

    public ServiceClassPolicySet() {
	super("ServiceClassPolicySet",
		PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS, null, null);
    }

    @Deprecated
    public static PolicySetType build() {
	return PolicySetHelper.buildWithAnyTarget("ServiceClassPolicySet",
		PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
    }

}
