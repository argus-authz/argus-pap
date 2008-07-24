package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.FunctionType;

public class FunctionHelper extends XMLObjectHelper<FunctionType> {

    private static final javax.xml.namespace.QName elementQName = FunctionType.DEFAULT_ELEMENT_NAME;
    private static FunctionHelper instance = new FunctionHelper();

    public static FunctionType build(String functionId) {
        
        FunctionType function = (FunctionType) builderFactory.getBuilder(
                elementQName).buildObject(elementQName);
        
        function.setFunctionId(functionId);
        
        return function;
    }

    public static FunctionHelper getInstance() {
        return instance;
    }

    private FunctionHelper() {}

}
