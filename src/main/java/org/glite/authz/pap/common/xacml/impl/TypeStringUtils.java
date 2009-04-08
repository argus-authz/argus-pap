package org.glite.authz.pap.common.xacml.impl;

import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for {@link PolicyType} and {@link PolicySetType} objects.
 */
public class TypeStringUtils {

    private static final Logger log = LoggerFactory.getLogger(TypeStringUtils.class);

    /**
     * Clone a <code>PolicySetType</code> object. The cloned object is an instance of
     * <code>PolicySetTypeString</code>.
     * 
     * @param policySet the object to be cloned.
     * @return the cloned object as an instance of {@Link PolicySetTypeString}.
     */
    public static PolicySetTypeString cloneAsPolicySetTypeString(PolicySetType policySet) {
        PolicySetTypeString policySetTypeString;

        if (policySet instanceof PolicySetTypeString) {

            policySetTypeString = new PolicySetTypeString(policySet.getPolicySetId(),
                                                          ((PolicySetTypeString) policySet).getPolicySetString());

        } else {

            policySetTypeString = new PolicySetTypeString(PolicySetHelper.getInstance().clone(policySet));
            policySetTypeString.releaseDOM();

            log.debug("Cloning a PolicySetType into a PolicySetTypeString");
        }
        return policySetTypeString;
    }

    /**
     * Clone a <code>PolicyType</code> object. The cloned object is an instance of
     * <code>PolicyTypeString</code>.
     * 
     * @param policy the object to be cloned.
     * @return the cloned object as an instance of {@Link PolicyTypeString}.
     */
    public static PolicyTypeString cloneAsPolicyTypeString(PolicyType policy) {
        PolicyTypeString policyTypeString;

        if (policy instanceof PolicyTypeString) {

            policyTypeString = new PolicyTypeString(policy.getPolicyId(),
                                                    ((PolicyTypeString) policy).getPolicyString());

        } else {

            policyTypeString = new PolicyTypeString(PolicyHelper.getInstance().clone(policy));
            policyTypeString.releaseDOM();

        }
        return policyTypeString;
    }

    /**
     * Release unneeded memory for the following objects: {@link PolicyTypeString},
     * {@link PolicySetTypeString}, {@link PolicyType}, {@link PolicySetType}, {@link PolicyWizard}
     * and {@link PolicySetWizard}.<br>
     * If the given object is not an instance of the above objects the method returns without doing
     * anything.
     * 
     * @param object an instance of: {@link PolicyTypeString}, {@link PolicySetTypeString},
     *            {@link PolicyType}, {@link PolicySetType}, {@link PolicyWizard} or
     *            {@link PolicySetWizard}.
     */
    public static void releaseUnneededMemory(Object object) {

        if (object instanceof PolicyTypeString) {

            ((PolicyTypeString) object).releaseDOM();

        } else if (object instanceof PolicySetTypeString) {

            ((PolicySetTypeString) object).releaseDOM();

        } else if (object instanceof PolicyType) {

            ((PolicyType) object).releaseChildrenDOM(true);
            ((PolicyType) object).releaseDOM();

        } else if (object instanceof PolicySetType) {

            ((PolicySetType) object).releaseChildrenDOM(true);
            ((PolicySetType) object).releaseDOM();

        } else if (object instanceof PolicyWizard) {

            ((PolicyWizard) object).releaseChildrenDOM();
            ((PolicyWizard) object).releaseDOM();

        } else if (object instanceof PolicySetWizard) {

            ((PolicySetWizard) object).releaseChildrenDOM();
            ((PolicySetWizard) object).releaseDOM();
        } else {
            log.warn("releaseUnnecessaryMemory(): unknown object " + object.getClass().getName());
        }
    }
}
