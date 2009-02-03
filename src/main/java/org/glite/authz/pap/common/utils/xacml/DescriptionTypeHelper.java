package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.DescriptionType;

public class DescriptionTypeHelper extends XMLObjectHelper<DescriptionType> {

    private static final javax.xml.namespace.QName elementQName = DescriptionType.DEFAULT_ELEMENT_NAME;
    private static DescriptionTypeHelper instance = new DescriptionTypeHelper();

    public static DescriptionType build(String value) {

        DescriptionType description = (DescriptionType) builderFactory.getBuilder(elementQName).buildObject(elementQName);

        description.setValue(value);

        return description;

    }

    public static DescriptionTypeHelper getInstance() {
        return instance;
    }

    private DescriptionTypeHelper() {}

}
