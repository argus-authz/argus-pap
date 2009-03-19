package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.utils.ActionHelper;
import org.glite.authz.pap.common.xacml.utils.ActionMatchHelper;
import org.glite.authz.pap.common.xacml.utils.ActionsHelper;
import org.glite.authz.pap.common.xacml.utils.EnvironmentHelper;
import org.glite.authz.pap.common.xacml.utils.EnvironmentMatchHelper;
import org.glite.authz.pap.common.xacml.utils.EnvironmentsHelper;
import org.glite.authz.pap.common.xacml.utils.ResourceHelper;
import org.glite.authz.pap.common.xacml.utils.ResourceMatchHelper;
import org.glite.authz.pap.common.xacml.utils.ResourcesHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectMatchHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectsHelper;
import org.glite.authz.pap.common.xacml.utils.TargetHelper;
import org.glite.authz.pap.common.xacml.wizard.exceptions.TargetWizardException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicyException;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.ActionMatchType;
import org.opensaml.xacml.policy.ActionType;
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.EnvironmentMatchType;
import org.opensaml.xacml.policy.EnvironmentsType;
import org.opensaml.xacml.policy.ResourceMatchType;
import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetWizard {

    private static final Logger log = LoggerFactory.getLogger(TargetWizard.class);

    private TargetType target = null;
    private final List<AttributeWizard> targetAttributeWizardList;

    public TargetWizard(AttributeWizard attributeWizard) {
        targetAttributeWizardList = new ArrayList<AttributeWizard>(1);
        targetAttributeWizardList.add(attributeWizard);
    }

    public TargetWizard(List<AttributeWizard> targetAttributeWizardList) {
        if (targetAttributeWizardList == null) {
            targetAttributeWizardList = new ArrayList<AttributeWizard>(0);
        }
        this.targetAttributeWizardList = targetAttributeWizardList;
    }

    public TargetWizard(TargetType target) {
        this.target = target;
        this.targetAttributeWizardList = buildAttributeWizardList(target);
    }

    private static List<AttributeWizard> buildAttributeWizardList(TargetType target) {

        List<AttributeWizard> attributeWizardList = new LinkedList<AttributeWizard>();
        List<AttributeType> attributeList = new LinkedList<AttributeType>();

        // get subjects
        SubjectsType subjects = target.getSubjects();

        if (subjects != null) {
            List<SubjectType> subjectList = subjects.getSubjects();

            if (!subjectList.isEmpty()) {

                if (subjectList.size() > 1) {
                    throw new UnsupportedPolicyException("Only one SubjectType is allowed");
                }

                attributeList.addAll(SubjectMatchHelper.getAttributeList(subjectList.get(0).getSubjectMatches()));
            }
        }

        // get resources
        ResourcesType resources = target.getResources();

        if (resources != null) {
            List<ResourceType> resourceList = resources.getResources();

            if (!resourceList.isEmpty()) {

                if (resourceList.size() > 1) {
                    throw new UnsupportedPolicyException("Only one ResourceSubjectType is allowed");
                }

                attributeList.addAll(ResourceMatchHelper.getAttributeList(resourceList.get(0).getResourceMatches()));
            }
        }

        // get actions
        ActionsType actions = target.getActions();

        if (actions != null) {
            List<ActionType> actionList = actions.getActions();

            if (!actionList.isEmpty()) {

                if (actionList.size() > 1) {
                    throw new UnsupportedPolicyException("Only one ActionSubjectType is allowed");
                }

                attributeList.addAll(ActionMatchHelper.getAttributeList(actionList.get(0).getActionMatches()));
            }
        }

        // build AttributeWizard list
        for (AttributeType attribute : attributeList) {
            attributeWizardList.add(new AttributeWizard(attribute));
        }

        return attributeWizardList;
    }

    public List<AttributeWizard> getAttributeWizardList() {
        return targetAttributeWizardList;
    }

    public TargetType getXACML() {
        initTargetTypeIfNotSet();
        return target;
    }

    public boolean isEquivalent(TargetType target) {

        if (target == null) {
            return false;
        }

        TargetWizard targetWizard;

        try {
            targetWizard = new TargetWizard(target);
        } catch (TargetWizardException e) {
            return false;
        }

        return isEquivalent(targetWizard);
    }

    public boolean isEquivalent(TargetWizard targetWizard) {

        List<AttributeWizard> attributeWizardList = targetWizard.getAttributeWizardList();
        if (targetAttributeWizardList.size() != attributeWizardList.size()) {
            return false;
        }

        for (AttributeWizard thisAttributeWizard : targetAttributeWizardList) {

            boolean found = false;

            for (AttributeWizard attributeWizard : attributeWizardList) {
                if (thisAttributeWizard.equals(attributeWizard)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                log.debug(thisAttributeWizard.toFormattedString() + " NOT FOUND");
                return false;
            }
        }
        return true;
    }

    public void releaseChildrenDOM() {
        if (target != null) {
            target.releaseChildrenDOM(true);
        }
    }

    public void releaseDOM() {
        if (target != null) {
            target.releaseDOM();
            target = null;
        }
    }

    private void initTargetTypeIfNotSet() {
        if (target == null) {
            setTargetType();
        }
    }

    private void setTargetType() {

        releaseDOM();

        List<SubjectMatchType> subjectMatchTypeList = new LinkedList<SubjectMatchType>();
        for (AttributeWizard attributeWizard : targetAttributeWizardList) {
            if (attributeWizard.isSubjectAttribute()) {
                subjectMatchTypeList.add(SubjectMatchHelper.buildWithDesignator(attributeWizard.getXACML(),
                                                                                attributeWizard.getMatchfunction()));
            }
        }

        List<ResourceMatchType> resourceMatchTypeList = new LinkedList<ResourceMatchType>();
        for (AttributeWizard attributeWizard : targetAttributeWizardList) {
            if (attributeWizard.isResourceAttribute()) {
                resourceMatchTypeList.add(ResourceMatchHelper.buildWithDesignator(attributeWizard.getXACML(),
                                                                                  attributeWizard.getMatchfunction()));
            }
        }

        List<ActionMatchType> actionMatchTypeList = new LinkedList<ActionMatchType>();
        for (AttributeWizard attributeWizard : targetAttributeWizardList) {
            if (attributeWizard.isActionAttribute()) {
                actionMatchTypeList.add(ActionMatchHelper.buildWithDesignator(attributeWizard.getXACML(),
                                                                              attributeWizard.getMatchfunction()));
            }
        }

        List<EnvironmentMatchType> environmentMatchTypeList = new LinkedList<EnvironmentMatchType>();
        for (AttributeWizard attributeWizard : targetAttributeWizardList) {
            if (attributeWizard.isEnvironmentAttribute()) {
                environmentMatchTypeList.add(EnvironmentMatchHelper.buildWithDesignator(attributeWizard.getXACML(),
                                                                                        attributeWizard.getMatchfunction()));
            }
        }

        SubjectsType subjects = SubjectsHelper.build(SubjectHelper.build(subjectMatchTypeList));
        ResourcesType resources = ResourcesHelper.build(ResourceHelper.build(resourceMatchTypeList));
        ActionsType actions = ActionsHelper.build(ActionHelper.build(actionMatchTypeList));
        EnvironmentsType environments = EnvironmentsHelper.build(EnvironmentHelper.build(environmentMatchTypeList));

        target = TargetHelper.build(subjects, actions, resources, environments);
    }
}
