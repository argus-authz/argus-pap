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

        return prefix + "-" + generateUUID();
    }

    public static List<AttributeType> getAttributes(List<AttributeWizard> list, AttributeWizardType.TargetElement type) {

        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (type.equals(attribute.getTargetElementType())) {
                resultList.add(attribute.getXACML());
            }
        }
        return resultList;
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
