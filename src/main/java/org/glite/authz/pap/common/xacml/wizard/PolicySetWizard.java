/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.impl.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.DescriptionTypeHelper;
import org.glite.authz.pap.common.xacml.utils.ObligationsHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
import org.glite.authz.pap.common.xacml.wizard.exceptions.PolicySetWizardException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicyException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.opensaml.xacml.policy.ObligationType;
import org.opensaml.xacml.policy.ObligationsType;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicySetWizard extends XACMLWizard {

    private static final Logger log = LoggerFactory.getLogger(PolicySetWizard.class);
    protected static final AttributeWizardType attributeWizardType = AttributeWizardTypeConfiguration.getInstance()
                                                                                                     .getResourceAttributeWizard();
    protected String description = null;
    protected final List<ObligationWizard> obligationWizardList = new LinkedList<ObligationWizard>();
    protected PolicySetType policySet = null;
    protected String policySetId = null;
    protected final List<PolicySetWizard> policySetWizardList;
    protected final List<PolicyWizard> policyWizardList;
    protected final String resourceValue;
    protected final TargetWizard targetWizard;
    protected String version = null;

    public PolicySetWizard(AttributeWizard attributeWizard) {

        if (attributeWizard.getAttributeWizardType() != attributeWizardType) {
            throw new UnsupportedPolicySetWizardException("Attribute not supported: "
                    + attributeWizard.getId());
        }

        resourceValue = attributeWizard.getValue();
        policySetId = WizardUtils.generateId(null);
        version = "1";
        targetWizard = new TargetWizard(attributeWizard);
        policyWizardList = new LinkedList<PolicyWizard>();
        policySetWizardList = new LinkedList<PolicySetWizard>();
    }

    public PolicySetWizard(PolicySetType policySet, List<PolicyWizard> policyWizardList,
            PolicySetType[] childPolicySetList) {

        this.policySet = policySet;
        policySetId = policySet.getPolicySetId();

        if (policySet.getDescription() != null) {
            description = policySet.getDescription().getValue();
        }

        targetWizard = new TargetWizard(policySet.getTarget());

        validateTargetAttributewizardList(targetWizard.getAttributeWizardList());

        resourceValue = targetWizard.getAttributeWizardList().get(0).getValue();

        try {
            version = policySet.getVersion();
            new Integer(version);
        } catch (NumberFormatException e) {
            throw new UnsupportedPolicyException(String.format("Wrong version format (policySetId=\"%s\")",
                                                               policySet.getPolicySetId()), e);
        }

        if (policySet.getObligations() != null) {
            List<ObligationType> obligationList = policySet.getObligations().getObligations();
            for (ObligationType obligation : obligationList) {
                obligationWizardList.add(new ObligationWizard(obligation));
            }
        }

        this.policyWizardList = new LinkedList<PolicyWizard>();
        policySetWizardList = new LinkedList<PolicySetWizard>();

        // add referenced policies
        List<String> idReferenceList = PolicySetHelper.getPolicyIdReferencesValues(policySet);

        if ((idReferenceList.size() > 0) && (policyWizardList == null)) {
            throw new PolicySetWizardException("policyList is null");
        }

        for (String policyIdReference : idReferenceList) {
            boolean found = false;
            for (PolicyWizard policyWizard : policyWizardList) {
                if (policyIdReference.equals(policyWizard.getPolicyId())) {
                    this.policyWizardList.add(policyWizard);
                    found = true;
                }
            }
            if (!found) {
                PolicySetHelper.deletePolicyReference(policySet, policyIdReference);
                log.warn("Not found policy reference: " + policyIdReference);
            }
        }

        // add referenced policy sets
        idReferenceList = PolicySetHelper.getPolicySetIdReferencesValues(policySet);

        if ((idReferenceList.size() > 0) && (childPolicySetList == null)) {
            throw new PolicySetWizardException("childPolicySetList is null");
        }

        for (String policySetIdReference : idReferenceList) {
            boolean found = false;
            for (PolicySetType childPolicySet : childPolicySetList) {
                if (policySetIdReference.equals(childPolicySet.getPolicySetId())) {
                    PolicySetWizard psw = new PolicySetWizard(childPolicySet,
                                                              policyWizardList,
                                                              childPolicySetList);
                    policySetWizardList.add(psw);
                    TypeStringUtils.releaseUnneededMemory(psw);
                    TypeStringUtils.releaseUnneededMemory(childPolicySet);
                    found = true;
                }
            }
            if (!found) {
                PolicySetHelper.deletePolicySetReference(policySet, policySetIdReference);
                log.warn("Not found policy set reference: " + policySetIdReference);
            }
        }
    }

    public static String getResourceValue(PolicySetType policySet) {

        TargetWizard targetWizard = new TargetWizard(policySet.getTarget());

        try {
            validateTargetAttributewizardList(targetWizard.getAttributeWizardList());
        } catch (UnsupportedPolicySetWizardException e) {
            return null;
        }
        
        return targetWizard.getAttributeWizardList().get(0).getValue();
    }

    public static void increaseVersion(PolicySetType policySet) {
        int version;

        try {
            version = (new Integer(policySet.getVersion())).intValue();
            version++;
        } catch (NumberFormatException e) {
            log.error("Unrecognized version format, setting version to 1. PolicySetId="
                    + policySet.getPolicySetId());
            version = 1;
        }

        policySet.setVersion(Integer.toString(version));
    }

    /**
     * @param targetAttributeWizardList
     * 
     * @throws UnsupportedPolicySetWizardException
     */
    private static void validateTargetAttributewizardList(List<AttributeWizard> targetAttributeWizardList) {

        if (targetAttributeWizardList.size() != 1) {
            throw new UnsupportedPolicySetWizardException("Wrong number of attributes, only one is supported");
        }

        AttributeWizard aw = targetAttributeWizardList.get(0);

        if (aw.getAttributeWizardType() != attributeWizardType) {
            throw new UnsupportedPolicySetWizardException("Only resource attributes are supported");
        }
    }

    public void addObligation(ObligationWizard obligationWizard) {
        obligationWizardList.add(obligationWizard);
        invalidatePolicySetType();
    }

    public void addPolicy(PolicyWizard policyWizard) {
        policyWizardList.add(policyWizard);
        invalidatePolicySetType();
    }

    public void addPolicySet(PolicySetWizard policySetWizard) {
        policySetWizardList.add(policySetWizard);
        invalidatePolicySetType();
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPolicyIdReferences() {

        List<String> idRefList = new ArrayList<String>(policyWizardList.size());

        for (PolicyWizard policyWizard : policyWizardList) {
            idRefList.add(policyWizard.getPolicyId());
        }

        return idRefList;
    }

    public String getPolicySetId() {
        return policySetId;
    }

    public List<String> getPolicySetIdReferences() {

        List<String> idRefList = new ArrayList<String>(policySetWizardList.size());

        for (PolicySetWizard policySetWizard : policySetWizardList) {
            idRefList.add(policySetWizard.getPolicySetId());
        }

        return idRefList;
    }

    public List<PolicyWizard> getPolicyWizardList() {
        return policyWizardList;
    }

    public String getTagAndValue() {
        return String.format("%s \"%s\"", attributeWizardType.getId(), resourceValue);
    }

    public TargetWizard getTargetWizard() {
        return targetWizard;
    }

    public int getVersion() {
        return Integer.valueOf(version);
    }

    public String getVersionString() {
        return version;
    }

    public PolicySetType getXACML() {
        initPolicySetTypeIfNotSet();
        return policySet;
    }

    public PolicySetType getXACMLNoReferences() {
        return buildXACMLNoReferences();
    }

    public void increaseVersion() {
        setVersion(getVersion() + 1);
    }

    public void releaseChildrenDOM() {
        targetWizard.releaseChildrenDOM();
        targetWizard.releaseDOM();
        for (ObligationWizard obligationWizard : obligationWizardList) {
            obligationWizard.releaseChildrenDOM();
            obligationWizard.releaseDOM();
        }
        for (PolicyWizard policyWizard : policyWizardList) {
            policyWizard.releaseChildrenDOM();
            policyWizard.releaseDOM();
        }
        for (PolicySetWizard policySetWizard : policySetWizardList) {
            policySetWizard.releaseChildrenDOM();
            policySetWizard.releaseDOM();
        }
    }

    public void releaseDOM() {
        if (policySet != null) {
            policySet.releaseDOM();
            policySet = null;
        }
    }

    public void setDescription(String value) {
        description = value;
        if (policySet != null) {
            policySet.setDescription(DescriptionTypeHelper.build(value));
        }
    }

    public void setPolicySetId(String id) {
        policySetId = id;
        if (policySet != null) {
            policySet.setPolicySetId(id);
        }
    }

    public void setVersion(int version) {
        this.version = Integer.toString(version);

        if (policySet != null) {
            policySet.setVersion(this.version);
        }
    }

    public boolean targetEquals(PolicySetWizard policySetWizard) {
        return targetWizard.equals(policySetWizard.getTargetWizard());
    }

    public String toFormattedString(boolean printIds) {
        return toFormattedString(0, 4, printIds, false);
    }

    public String toFormattedString(boolean printIds, boolean printRulesId) {
        return toFormattedString(0, 4, printIds, printRulesId);
    }

    public String toFormattedString(int baseIndentation, int internalIndentation, boolean printIds,
            boolean printRulesId) {

        String baseIndentString = Utils.fillWithSpaces(baseIndentation);
        String indentString = Utils.fillWithSpaces(baseIndentation + internalIndentation);
        StringBuffer sb = new StringBuffer();

        if (printIds) {
            sb.append(String.format("%sid=%s\n", baseIndentString, policySetId));
        }

        sb.append(String.format("%sresource \"%s\" {\n", baseIndentString, resourceValue));

        if (description != null) {
            sb.append(String.format("%sdescription=\"%s\"\n", indentString, description));
        }

        for (ObligationWizard obligationWizard : obligationWizardList) {
            sb.append(obligationWizard.toFormattedString(baseIndentation + internalIndentation,
                                                         internalIndentation));
            sb.append('\n');
        }

        for (PolicyWizard policyWizard : policyWizardList) {
            sb.append('\n');
            sb.append(policyWizard.toFormattedString(baseIndentation + internalIndentation,
                                                     internalIndentation,
                                                     printIds,
                                                     printRulesId));
            sb.append('\n');

        }

        sb.append(baseIndentString + "}");

        return sb.toString();
    }

    public String toXACMLString() {
        initPolicySetTypeIfNotSet();

        StringBuffer sb = new StringBuffer();

        sb.append(XMLObjectHelper.toString(policySet));
        sb.append('\n');

        for (PolicySetWizard policySetWizard : policySetWizardList) {
            sb.append(policySetWizard.toXACMLString());
            sb.append('\n');
        }

        for (PolicyWizard policyWizard : policyWizardList) {
            sb.append(policyWizard.toXACMLString());
            sb.append('\n');
        }
        return sb.toString();
    }

    private PolicySetTypeString buildXACMLNoReferences() {
        PolicySetTypeString policySet = new PolicySetTypeString(PolicySetHelper.build(policySetId,
                                                                                      PolicySetHelper.COMB_ALG_FIRST_APPLICABLE,
                                                                                      targetWizard.getXACML(),
                                                                                      null));
        if (description != null) {
            policySet.setDescription(DescriptionTypeHelper.build(description));
        }

        policySet.setVersion(version);

        return policySet;
    }

    private void initPolicySetTypeIfNotSet() {
        if (policySet == null) {
            setPolicySetType(true);
        }
    }

    private void invalidatePolicySetType() {
        releaseChildrenDOM();
        releaseDOM();
    }

    private void setPolicySetType(boolean includeReferences) {

        releaseDOM();

        policySet = buildXACMLNoReferences();

        if (obligationWizardList.size() > 0) {
            ObligationsType obligations = ObligationsHelper.build();
            List<ObligationType> obligationList = obligations.getObligations();
            for (ObligationWizard obligationWizard : obligationWizardList) {
                obligationList.add(obligationWizard.getXACML());
            }

            policySet.setObligations(obligations);
        }

        if (includeReferences) {
            for (PolicySetWizard policySetWizard : policySetWizardList) {
                PolicySetHelper.addPolicySetReference(policySet, policySetWizard.getPolicySetId());
            }

            for (PolicyWizard policyWizard : policyWizardList) {
                PolicySetHelper.addPolicyReference(policySet, policyWizard.getPolicyId());
            }
        }
    }
}
