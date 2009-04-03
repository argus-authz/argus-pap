package org.glite.authz.pap.authz.operations.highlevelpolicymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.HighLevelPolicyManagementServiceException;

public class PurgeOperation extends BasePAPOperation<Object> {

    private String alias;
    private boolean purgeUnreferencedPolicies;
    private boolean purgeEmptyPolicies;
    private boolean purgeUnreferencedPolicySets;
    private boolean purgeEmptyPolicySets;

    protected PurgeOperation(String alias, boolean purgeUnreferencedPolicies, boolean purgeEmptyPolicies,
            boolean purgeUnreferencedPolicySets, boolean purgeEmptyPolicySets) {
        this.alias = alias;
        this.purgeUnreferencedPolicies = purgeUnreferencedPolicies;
        this.purgeEmptyPolicies = purgeEmptyPolicies;
        this.purgeUnreferencedPolicySets = purgeUnreferencedPolicySets;
        this.purgeEmptyPolicySets = purgeEmptyPolicySets;
    }

    public static PurgeOperation instance(String alias, boolean purgeUnreferencedPolicies,
            boolean purgeEmptyPolicies, boolean purgeUnreferencedPolicySets, boolean purgeEmptyPolicySets) {
        return new PurgeOperation(alias,
                                  purgeUnreferencedPolicies,
                                  purgeEmptyPolicies,
                                  purgeUnreferencedPolicySets,
                                  purgeEmptyPolicySets);
    }

    protected Object doExecute() {

        if (alias == null) {
            alias = PAP.DEFAULT_PAP_ALIAS;
        }

        PAP pap = PAPManager.getInstance().getPAP(alias);

        if (pap.isRemote()) {
            throw new HighLevelPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PAPContainer papContainer = new PAPContainer(pap);

        if (purgeUnreferencedPolicies) {
            papContainer.purgeUnreferencesPolicies();
        }

        if (purgeEmptyPolicies) {
            papContainer.purgePoliciesWithNoRules();
        }

        if (purgeUnreferencedPolicySets) {
            papContainer.purgeUnreferencedPolicySets();
        }

        if (purgeEmptyPolicySets) {
            papContainer.purgePolicySetWithNoPolicies();
        }
        return null;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE,
                                               PermissionFlags.POLICY_READ_LOCAL));
    }
}
