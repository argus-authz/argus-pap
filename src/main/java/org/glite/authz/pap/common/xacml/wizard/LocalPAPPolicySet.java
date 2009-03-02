package org.glite.authz.pap.common.xacml.wizard;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;

public class LocalPAPPolicySet extends PolicySetWizard {

    public LocalPAPPolicySet() {
        super(PAP.LOCAL_PAP_ALIAS, PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS, null, null);
    }

}
