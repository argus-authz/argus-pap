package org.glite.authz.pap.common.xacml.wizard;

import java.util.Arrays;
import java.util.Collections;
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
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedAttributeException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedTargetException;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.ActionMatchType;
import org.opensaml.xacml.policy.ActionType;
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.EnvironmentMatchType;
import org.opensaml.xacml.policy.EnvironmentType;
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

/**
 * This class renders a list of {@link AttributeWizard} into an XACML Target and vice versa.
 * Supported targets are the ones with single SubjectType, ResourceType, ActionType and
 * EnvironmentType elements.
 */
public class TargetWizard {

    private static final Logger log = LoggerFactory.getLogger(TargetWizard.class);

    private TargetType target = null;
    private final List<AttributeWizard> targetAttributeWizardList;

    /**
     * Constructor.
     * 
     * @param attributeWizard the attribute wizard to build the Target with.
     */
    public TargetWizard(AttributeWizard attributeWizard) {
        targetAttributeWizardList = Arrays.asList(attributeWizard);
    }

    /**
     * Constructor.
     * <p>
     * Attributes of the same category (resource, action, subject, environment) are
     * <i>Category</i>Match elements of the generated target. For example if the given list of
     * attributes as two subject attributes the generated Target contains two <i>SubjectMatch</i>
     * elements.
     * 
     * @param targetAttributeWizardList the list of attribute wizard to build the Target with.
     */
    public TargetWizard(List<AttributeWizard> targetAttributeWizardList) {
        if (targetAttributeWizardList == null) {
            this.targetAttributeWizardList = Collections.emptyList();
        } else {
            this.targetAttributeWizardList = targetAttributeWizardList;
        }
    }

    /**
     * Constructor.
     * 
     * @param target the XACML target.
     */
    public TargetWizard(TargetType target) {
        this.target = target;
        this.targetAttributeWizardList = buildAttributeWizardList(target);
    }

    /**
     * Build a list of attribute wizard from the given XACML Target.
     * <p>
     * Supported targets are the ones with single SubjectType, ResourceType, ActionType and
     * EnvironmentType elements.
     * 
     * @param target the XACML Target.
     * @return a list of attribute wizard from the given XACML Target.
     * 
     * @throws UnsupportedTargetException if the given XACML Target is not recognized.
     * @throws UnsupportedAttributeException if the given XACML Target have some attributes that are
     *             not recognized.
     */
    private static List<AttributeWizard> buildAttributeWizardList(TargetType target) {

        List<AttributeWizard> attributeWizardList = new LinkedList<AttributeWizard>();
        List<AttributeType> attributeList = new LinkedList<AttributeType>();

        // get subjects
        SubjectsType subjects = target.getSubjects();

        if (subjects != null) {
            List<SubjectType> subjectList = subjects.getSubjects();

            if (!subjectList.isEmpty()) {

                if (subjectList.size() > 1) {
                    throw new UnsupportedTargetException("Only one SubjectType is allowed");
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
                    throw new UnsupportedTargetException("Only one ResourceType is allowed");
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
                    throw new UnsupportedTargetException("Only one ActionType is allowed");
                }

                attributeList.addAll(ActionMatchHelper.getAttributeList(actionList.get(0).getActionMatches()));
            }
        }

        // get environments
        EnvironmentsType environments = target.getEnvironments();

        if (environments != null) {
            List<EnvironmentType> environmentList = environments.getEnvrionments();

            if (!environmentList.isEmpty()) {

                if (environmentList.size() > 1) {
                    throw new UnsupportedTargetException("Only one EnvironmentType is allowed");
                }

                attributeList.addAll(EnvironmentMatchHelper.getAttributeList(environmentList.get(0).getEnvrionmentMatches()));
            }
        }

        // build AttributeWizard list
        for (AttributeType attribute : attributeList) {
            attributeWizardList.add(new AttributeWizard(attribute));
        }

        return attributeWizardList;
    }

    /**
     * Return the list of attributes (as <code>AttributeWizard</code> objects) of this Target.
     * 
     * @return the list of attributes (as <code>AttributeWizard</code> objects) of this Target.
     */
    public List<AttributeWizard> getAttributeWizardList() {
        return targetAttributeWizardList;
    }

    /**
     * Return the <code>TargetType</code> object.
     * @return the <code>TargetType</code> object.
     */
    public TargetType getXACML() {
        initTargetTypeIfNotSet();
        return target;
    }

    /**
     * Check if this target is equivalent to the given one.
     * <p>
     * Two Targets are considered to be equivalent if they have the same list of attributes.
     * 
     * @param target the <code>TargetType</code> to compare this target against.
     * @return <code>true</code> if this Target and the given one are equivalent, <code>false</code>
     *         otherwise.
     */
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

    /**
     * Check if this target is equivalent to the given one.
     * <p>
     * Two Targets are considered to be equivalent if they have the same list of attributes.
     * 
     * @param targetWizard the <code>TargetWizard</code> to compare this target against.
     * @return <code>true</code> if this Target and the given one are equivalent, <code>false</code>
     *         otherwise.
     */
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

    /**
     * Release children DOM of the internal <code>TargetType</code> object.
     */
    public void releaseChildrenDOM() {
        if (target != null) {
            target.releaseChildrenDOM(true);
        }
    }

    /**
     * Release DOM of the internal <code>TargetType</code> object.
     */
    public void releaseDOM() {
        if (target != null) {
            target.releaseDOM();
            target = null;
        }
    }

    /**
     * Build the <code>TargetType</code> object if it doesn't exist.
     */
    private void initTargetTypeIfNotSet() {
        if (target == null) {
            setTargetType();
        }
    }

    /**
     * Build the <code>TargetType</code> object replacing the existing one (if any).
     */
    private void setTargetType() {

        releaseDOM();

        List<SubjectMatchType> subjectMatchTypeList = new LinkedList<SubjectMatchType>();
        for (AttributeWizard attributeWizard : targetAttributeWizardList) {
            if (attributeWizard.isSubjectAttribute()) {
                subjectMatchTypeList.add(SubjectMatchHelper.buildWithDesignator(attributeWizard.getXACML(),
                                                                                attributeWizard.getMatchfunction(),
                                                                                attributeWizard.getMatchFunctionDataType()
                                                                            	));
            }
        }

        List<ResourceMatchType> resourceMatchTypeList = new LinkedList<ResourceMatchType>();
        for (AttributeWizard attributeWizard : targetAttributeWizardList) {
            if (attributeWizard.isResourceAttribute()) {
                resourceMatchTypeList.add(ResourceMatchHelper.buildWithDesignator(attributeWizard.getXACML(),
                                                                                  attributeWizard.getMatchfunction(),
                                                                                  attributeWizard.getMatchFunctionDataType()
                                                                                  
                ));
            }
        }

        List<ActionMatchType> actionMatchTypeList = new LinkedList<ActionMatchType>();
        for (AttributeWizard attributeWizard : targetAttributeWizardList) {
            if (attributeWizard.isActionAttribute()) {
                actionMatchTypeList.add(ActionMatchHelper.buildWithDesignator(attributeWizard.getXACML(),
                                                                              attributeWizard.getMatchfunction(),
                                                                              attributeWizard.getMatchFunctionDataType()
                                                                              ));
            }
        }

        List<EnvironmentMatchType> environmentMatchTypeList = new LinkedList<EnvironmentMatchType>();
        for (AttributeWizard attributeWizard : targetAttributeWizardList) {
            if (attributeWizard.isEnvironmentAttribute()) {
                environmentMatchTypeList.add(EnvironmentMatchHelper.buildWithDesignator(attributeWizard.getXACML(),
                                                                                        attributeWizard.getMatchfunction(),
                                                                                        attributeWizard.getMatchFunctionDataType()));
            }
        }

        SubjectsType subjects = SubjectsHelper.build(SubjectHelper.build(subjectMatchTypeList));
        ResourcesType resources = ResourcesHelper.build(ResourceHelper.build(resourceMatchTypeList));
        ActionsType actions = ActionsHelper.build(ActionHelper.build(actionMatchTypeList));
        EnvironmentsType environments = EnvironmentsHelper.build(EnvironmentHelper.build(environmentMatchTypeList));

        target = TargetHelper.build(subjects, actions, resources, environments);
    }
}
