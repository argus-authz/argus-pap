package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.ApplyType;

public class ApplyHelper extends XACMLHelper<ApplyType> {

    public static final String RULE_COMBALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
    public static final String RULE_COMBALG_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides";
    public static final String RULE_COMBALG_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";

    private static final javax.xml.namespace.QName elementQName = ApplyType.DEFAULT_ELEMENT_NAME;

    private static ApplyHelper instance = new ApplyHelper();

    public static ApplyType buildFunctionAnd() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.AND);

        return apply;
    }

    public static ApplyType buildFunctionAnyOf() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.ANY_OF);

        return apply;
    }

    public static ApplyType buildFunctionAnyOfAll() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.ANY_OF_ALL);

        return apply;
    }

    public static ApplyType buildFunctionAnyOfAny() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.ANY_OF_ANY);

        return apply;
    }

    public static ApplyType buildFunctionNot() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.NOT);
        return apply;
    }

    public static ApplyType buildFunctionOr() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.OR);

        return apply;
    }

    public static ApplyType buildFunctionStringBag() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.STRING_BAG);

        return apply;
    }

    public static ApplyHelper getInstance() {
        return instance;
    }

    private ApplyHelper() {}

}
