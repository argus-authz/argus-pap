package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.opensaml.xacml.ctx.AttributeType;

public class WizardUtils {
    
    public static String generateId(String prefix) {
        
        String id = generateUUID();
        
        if (prefix == null) {
            return id;
        }
        
        if (prefix.length() == 0) {
            return id;
        }
        
        return prefix + "_" + generateUUID();
    }
    
    public static List<AttributeType> getActions(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isActionAttribute()) {
                resultList.add(attribute.getXACML());
            }
        }

        return resultList;
    }
    
    public static List<AttributeType> getEnvironmentAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isEnvironmentAttribute()) {
                resultList.add(attribute.getXACML());
            }
        }

        return resultList;
    }

    public static List<AttributeType> getResourceAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isResourceAttribute()) {
                resultList.add(attribute.getXACML());
            }
        }

        return resultList;
    }

    public static List<AttributeType> getSubjectAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isSubjectAttribute()) {
                resultList.add(attribute.getXACML());
            }
        }

        return resultList;
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
