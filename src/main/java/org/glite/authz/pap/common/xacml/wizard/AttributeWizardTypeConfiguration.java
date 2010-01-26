package org.glite.authz.pap.common.xacml.wizard;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizardType.TargetElement;
import org.glite.authz.pap.common.xacml.wizard.exceptions.AttributeWizardTypeConfigurationException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedAttributeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads the supported {@link AttributeWizardType} from configuration.
 * <p>
 * Call the {@link AttributeWizardTypeConfiguration#bootstrap()} method before anything else.
 */
public class AttributeWizardTypeConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AttributeWizardTypeConfiguration.class);

    private static AttributeWizardType actionPolicyAttributeWizard = null;
    private static AttributeWizardType resourcePolicySetAttributeWizard = null;
    private static final String UNRECOGNIZED_ATTRIBUTE_ID = "unrecognized-id";

    private static AttributeWizardTypeConfiguration instance = null;
    private static List<AttributeWizardType> list = null;

    private static String baseKey(String id) {
        return id;
    }

    public static void bootstrap(String attributeMappingsFileName) {

        list = new LinkedList<AttributeWizardType>();

        initListFromFile(attributeMappingsFileName);

        initResourcePolicySetAttributeWizard();
        initActionPolicyAttributeWizard();
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
    
    private static void initListFromFile(String fileName) {

        File attributeMappingsFile = new File (fileName);
        
        if (!attributeMappingsFile.exists()) {
            throw new AttributeWizardTypeConfigurationException("Configuration file not found: " + attributeMappingsFile.getAbsolutePath());
        }

        PropertiesConfiguration configuration;

        try {
            configuration = new PropertiesConfiguration(attributeMappingsFile);
        } catch (ConfigurationException e) {
            throw new AttributeWizardTypeConfigurationException("Error reading configuration: "
                    + attributeMappingsFile.getAbsolutePath());
        }

        String[] idArray = configuration.getStringArray(idKey());

        for (String id : idArray) {

            if (checkIdExist(id)) {
                throw new AttributeWizardTypeConfigurationException("Error: duplicated id \"" + id + "\" ("
                        + attributeMappingsFile.getAbsolutePath() + ")");
            }

            String xacmlId = configuration.getString(xacmlIdKey(id));
            if (xacmlId == null) {
                throw new AttributeWizardTypeConfigurationException("Error: undefined xacml-id for id \"" + id + "\" ("
                        + attributeMappingsFile.getAbsolutePath() + ")");
            }

            String xacmlDataType = configuration.getString(xacmlDataTypeKey(id));
            if (xacmlDataType == null) {
                throw new AttributeWizardTypeConfigurationException("Error: undefined xacml-data-type for id \"" + id
                        + "\" (" + attributeMappingsFile.getAbsolutePath() + ")");
            }

            String xacmlMatchFunction = configuration.getString(xacmlMatchFunctionKey(id));
            if (xacmlMatchFunction == null) {
                throw new AttributeWizardTypeConfigurationException("Error: undefined xacml-match-function for id \"" + id
                        + "\" (" + attributeMappingsFile.getAbsolutePath() + ")");
            }

            String xacmlTargetElement = configuration.getString(xacmlTargetElementKey(id));
            if (xacmlTargetElement == null) {
                throw new AttributeWizardTypeConfigurationException("Error: undefined xacml-target-element for id \"" + id
                        + "\" (" + attributeMappingsFile.getAbsolutePath() + ")");
            }

            if (checkXacmlIdExist(xacmlId)) {
                throw new AttributeWizardTypeConfigurationException("Error: duplicated xacml-id \"" + xacmlId + "\" ("
                        + attributeMappingsFile.getAbsolutePath() + ")");
            }
            
            String xacmlMatchFunctionDataType = configuration.getString(xacmlMatchFunctionDataTypeKey(id)); 

            
            log.info(String.format("Adding new AttributeWizardType: id=%s, xacml-id=%s, xacml-data-type=%s, xacml-target-element=%s, xacml-match-function=%s, xacml-match-function-data-type=%s",
                                   id,
                                   xacmlId,
                                   xacmlDataType,
                                   xacmlTargetElement,
                                   xacmlMatchFunction,
                                   xacmlMatchFunctionDataType));

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

            list.add(new AttributeWizardType(id, xacmlId, xacmlDataType, targetElement, xacmlMatchFunction, xacmlMatchFunctionDataType));
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

    private static String xacmlMatchFunctionDataTypeKey(String id){
    	return baseKey(id) + ".xacml-match-function-datatype";
    }
    private static String xacmlTargetElementKey(String id) {
        return baseKey(id) + ".xacml-target-element";
    }

    /**
     * Return the attribute used for the actions of the simplified policy language.
     * 
     * @return the attribute used for the actions of the simplified policy language.
     */
    public AttributeWizardType getActionAttributeWizard() {
        return actionPolicyAttributeWizard;
    }

    /**
     * Return the <code>AtributeWizardType</code> identified by the given id of the simplified
     * policy language.
     * 
     * @param id attribute id of the simplified policy language.
     * @return the <code>AtributeWizardType</code> identified by the given id of the simplified
     *         policy language.
     * 
     * @throws UnsupportedAttributeException if the given id is not supported.
     */
    public AttributeWizardType getById(String id) {

        for (AttributeWizardType attributeWizardType : list) {
            if (attributeWizardType.getId().equals(id)) {
                return attributeWizardType;
            }
        }

        throw new UnsupportedAttributeException("id=" + id);
    }

    /**
     * Return the <code>AtributeWizardType</code> identified by the given XACML id.
     * 
     * @param id XACML attribute id.
     * @return the <code>AtributeWizardType</code> identified by the given XACML id.
     * 
     * @throws UnsupportedAttributeException if the given id is not supported.
     */
    public AttributeWizardType getByXACMLId(String xacmlId) {

        for (AttributeWizardType attributeWizardType : list) {
            if (attributeWizardType.getXacmlId().equals(xacmlId)) {
                return attributeWizardType;
            }
        }

        throw new UnsupportedAttributeException("xacmlId=" + xacmlId);
    }

    /**
     * Return the attribute used for the resources of the simplified policy language.
     * 
     * @return the attribute used for the resources of the simplified policy language.
     */
    public AttributeWizardType getResourceAttributeWizard() {
        return resourcePolicySetAttributeWizard;
    }
    
    /**
     * Return the <code>AttributeWizardType</code> identifying an unrecognized XACML attribute.
     * 
     * @param xacmlId XACML id.
     * @param dataType data type of the attribute.
     * @return the <code>AttributeWizardType</code> identifying an unrecognized XACML attribute.
     */
    public AttributeWizardType getUnrecognizedAttributeWizard(String xacmlId, String dataType) {
        return new AttributeWizardType(UNRECOGNIZED_ATTRIBUTE_ID, xacmlId, dataType, null, null,null);
    }

    /**
     * Checks whether the given id (of the simplified policy language) is supported or not.
     * 
     * @param id the id to check.
     * @return <code>true</code> if the given id (of the simplified policy language) is supported,
     *         <code>false</code> otherwise.
     */
    public boolean idExist(String id) {
        return checkIdExist(id);
    }

    /**
     * Checks whether the given XACML id is supported or not.
     * 
     * @param id the XACML id to check.
     * @return <code>true</code> if the given XACML id is supported, <code>false</code> otherwise.
     */
    public boolean xacmlIdExist(String xacmlId) {
        return checkXacmlIdExist(xacmlId);
    }

    /**
     * Checks whether the given XACML id is of the same type of the given <code>TargetElement</code>
     * (e.g. a subject, resource, action or environment attribute).
     * 
     * @param xacmlId the XACML id to check.
     * @param targetElement the target element type to check against.
     * @return <code>true</code> is the given XACML id is of the same type of the given
     *         <code>TargetElement</code>, <code>false</code> otherwise (even in the case that the
     *         given XACML is not supported, i.e. not found in the configuration).
     */
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
