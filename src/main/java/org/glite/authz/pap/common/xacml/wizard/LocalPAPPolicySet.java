package org.glite.authz.pap.common.xacml.wizard;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;

public class LocalPAPPolicySet extends PolicySetWizard {

    public LocalPAPPolicySet() {
        super(PAP.localPAPAlias, PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS, null, null);
    }

}
