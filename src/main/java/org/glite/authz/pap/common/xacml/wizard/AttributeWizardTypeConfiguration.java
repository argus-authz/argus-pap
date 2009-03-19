package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.INIConfiguration;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizardType.TargetElement;
import org.glite.authz.pap.common.xacml.wizard.exceptions.AttributeWizardTypeConfigurationException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedAttributeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeWizardTypeConfiguration {

    private static AttributeWizardType actionPolicyAttributeWizard = null;

    private static AttributeWizardTypeConfiguration instance = null;
    private static List<AttributeWizardType> list = null;
    private static final Logger log = LoggerFactory.getLogger(AttributeWizardTypeConfiguration.class);
    private static AttributeWizardType resourcePolicySetAttributeWizard = null;

    public static void bootstrap() {

        list = new LinkedList<AttributeWizardType>();

        initListFromFile();

        initResourcePolicySetAttributeWizard();
        initActionPolicyAttributeWizard();
    }

    public static AttributeWizardTypeConfiguration getInstance() {
        if (list == null) {
            throw new AttributeWizardTypeConfigurationException(
                "Initialization is required (call bootstrap()) before calling the getInstance method");
        }
        if (instance == null) {
            instance = new AttributeWizardTypeConfiguration();
        }
        return instance;
    }

    private static String baseKey(String id) {
        return id;
    }

    private static boolean checkIdExist(String id) {
        for (AttributeWizardType attributeWizardType : list) {
            if (attributeWizardType.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkXacmlIdExist(String xacmlId) {
        for (AttributeWizardType attributeWizardType : list) {
            if (attributeWizardType.getXacmlId().equals(xacmlId)) {
                return true;
            }
        }
        return false;
    }

    private static String idKey() {
        return "id";
    }

    private static void initActionPolicyAttributeWizard() {

        for (AttributeWizardType attributeWizardType : list) {
            if ("action".equals(attributeWizardType.getId())) {
                actionPolicyAttributeWizard = attributeWizardType;
                return;
            }
        }
        throw new AttributeWizardTypeConfigurationException("Cannot find id \"action\"");
    }

    private static void initListFromFile() {

        String attributeMappingsFile = "/configuration/attribute-mappings.ini";

        INIConfiguration configuration;

        try {
            configuration = new INIConfiguration(Object.class.getResource(attributeMappingsFile));
        } catch (ConfigurationException e) {
            throw new AttributeWizardTypeConfigurationException("Error reading configuration from resource: "
                    + attributeMappingsFile);
        }

        String[] idArray = configuration.getStringArray(idKey());

        for (String id : idArray) {

            if (checkIdExist(id)) {
                throw new AttributeWizardTypeConfigurationException("Error: duplicated id \"" + id + "\" (resource: "
                        + attributeMappingsFile + ")");
            }

            String xacmlId = configuration.getString(xacmlIdKey(id));
            if (xacmlId == null) {
                throw new AttributeWizardTypeConfigurationException("Error: undefined xacml-id for id \"" + id + "\" (resource: "
                        + attributeMappingsFile + ")");
            }

            String xacmlDataType = configuration.getString(xacmlDataTypeKey(id));
            if (xacmlDataType == null) {
                throw new AttributeWizardTypeConfigurationException("Error: undefined xacml-data-type for id \"" + id
                        + "\" (resource: " + attributeMappingsFile + ")");
            }

            String xacmlMatchFunction = configuration.getString(xacmlMatchFunctionKey(id));
            if (xacmlMatchFunction == null) {
                throw new AttributeWizardTypeConfigurationException("Error: undefined xacml-match-function for id \"" + id
                        + "\" (resource: " + attributeMappingsFile + ")");
            }

            String xacmlTargetElement = configuration.getString(xacmlTargetElementKey(id));
            if (xacmlTargetElement == null) {
                throw new AttributeWizardTypeConfigurationException("Error: undefined xacml-target-element for id \"" + id
                        + "\" (resource: " + attributeMappingsFile + ")");
            }

            if (checkXacmlIdExist(xacmlId)) {
                throw new AttributeWizardTypeConfigurationException("Error: duplicated xacml-id \"" + xacmlId + "\" (resource: "
                        + attributeMappingsFile + ")");
            }

            log.info(String.format("Adding new AttributeWizardType: id=%s, xacml-id=%s, xacml-data-type=%s, xacml-target-element=%s, xacml-mach-function=%s",
                                   id,
                                   xacmlId,
                                   xacmlDataType,
                                   xacmlTargetElement,
                                   xacmlMatchFunction));

            AttributeWizardType.TargetElement targetElement;

            if ("subject".equals(xacmlTargetElement.toLowerCase())) {
                targetElement = AttributeWizardType.TargetElement.SUBJECT;
            } else if ("resource".equals(xacmlTargetElement.toLowerCase())) {
                targetElement = AttributeWizardType.TargetElement.RESOURCE;
            } else if ("action".equals(xacmlTargetElement.toLowerCase())) {
                targetElement = AttributeWizardType.TargetElement.ACTION;
            } else if ("environment".equals(xacmlTargetElement.toLowerCase())) {
                targetElement = AttributeWizardType.TargetElement.ENVIRONMENT;
            } else {
                throw new AttributeWizardTypeConfigurationException("Error: xacml-target-element \"" + xacmlTargetElement
                        + "\" not recognized " + "(allowed values are: subject, resource, action and environment");
            }

            list.add(new AttributeWizardType(id, xacmlId, xacmlDataType, targetElement, xacmlMatchFunction));
        }
    }

    private static void initResourcePolicySetAttributeWizard() {

        for (AttributeWizardType attributeWizardType : list) {
            if ("resource".equals(attributeWizardType.getId())) {
                resourcePolicySetAttributeWizard = attributeWizardType;
                return;
            }
        }
        throw new AttributeWizardTypeConfigurationException("Cannot find id \"resource\"");
    }

    private static String xacmlDataTypeKey(String id) {
        return baseKey(id) + ".xacml-datatype";
    }

    private static String xacmlIdKey(String id) {
        return baseKey(id) + ".xacml-id";
    }

    private static String xacmlMatchFunctionKey(String id) {
        return baseKey(id) + ".xacml-match-function";
    }

    private static String xacmlTargetElementKey(String id) {
        return baseKey(id) + ".xacml-target-element";
    }

    public AttributeWizardType getActionAttributeWizard() {
        return actionPolicyAttributeWizard;
    }

    public AttributeWizardType getById(String id) {

        for (AttributeWizardType attributeWizardType : list) {
            if (attributeWizardType.getId().equals(id)) {
                return attributeWizardType;
            }
        }

        throw new UnsupportedAttributeException("id=" + id);
    }

    public AttributeWizardType getByXACMLId(String xacmlId) {

        for (AttributeWizardType attributeWizardType : list) {
            if (attributeWizardType.getXacmlId().equals(xacmlId)) {
                return attributeWizardType;
            }
        }

        throw new UnsupportedAttributeException("xacmlId=" + xacmlId);
    }

    public AttributeWizardType getResourceAttributeWizard() {
        return resourcePolicySetAttributeWizard;
    }

    public boolean idExist(String id) {
        return checkIdExist(id);
    }

    public boolean xacmlIdExist(String xacmlId) {
        return checkXacmlIdExist(xacmlId);
    }

    public boolean xacmlIdMatchesTargetElement(String xacmlId, TargetElement targetElement) {
        try {

            AttributeWizardType attributeWizardType = getByXACMLId(xacmlId);

            if (attributeWizardType.getTargetElement().equals(targetElement)) {
                return true;
            }

        } catch (UnsupportedAttributeException e) {
            // return false
        }
        return false;
    }

}
