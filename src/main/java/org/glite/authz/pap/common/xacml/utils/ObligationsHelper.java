package org.glite.authz.pap.common.xacml.utils;

import org.opensaml.xacml.policy.ObligationsType;

public class ObligationsHelper extends XMLObjectHelper<ObligationsType> {

    private static final javax.xml.namespace.QName elementQName = ObligationsType.DEFAULT_ELEMENT_QNAME;
    private static final ObligationsHelper instance = new ObligationsHelper();

    private ObligationsHelper() {}

    public static ObligationsType build() {
        ObligationsType obligations = (ObligationsType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        return obligations;
    }
    
    public static ObligationsHelper getInstance() {
        return instance;
    }

}
