package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.ApplyType;
import org.opensaml.xml.Configuration;

public class ApplyHelper extends XACMLHelper<ApplyType> {

    public static final String RULE_COMBALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
    public static final String RULE_COMBALG_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides";
    public static final String RULE_COMBALG_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";

    private static ApplyHelper instance = null;

    public static ApplyType buildFunctionNot() {

	ApplyType apply = (ApplyType) Configuration.getBuilderFactory()
		.getBuilder(ApplyType.DEFAULT_ELEMENT_NAME).buildObject(
			ApplyType.DEFAULT_ELEMENT_NAME);
	apply.setFunctionId(Functions.NOT);
	return apply;
    }

    public static ApplyType buildFunctionOr() {

	ApplyType apply = (ApplyType) Configuration.getBuilderFactory()
		.getBuilder(ApplyType.DEFAULT_ELEMENT_NAME).buildObject(
			ApplyType.DEFAULT_ELEMENT_NAME);
	apply.setFunctionId(Functions.OR);
	return apply;
    }

    public static ApplyType buildFunctionAnyOfAll() {

	ApplyType apply = (ApplyType) Configuration.getBuilderFactory()
		.getBuilder(ApplyType.DEFAULT_ELEMENT_NAME).buildObject(
			ApplyType.DEFAULT_ELEMENT_NAME);
	apply.setFunctionId(Functions.ANY_OF_ALL);
	return apply;
    }

    public static ApplyType buildFunctionStringBag() {

	ApplyType apply = (ApplyType) Configuration.getBuilderFactory()
		.getBuilder(ApplyType.DEFAULT_ELEMENT_NAME).buildObject(
			ApplyType.DEFAULT_ELEMENT_NAME);
	apply.setFunctionId(Functions.STRING_BAG);
	return apply;
    }

    public static ApplyHelper getInstance() {
	if (instance == null) {
	    instance = new ApplyHelper();
	}
	return instance;
    }

    private ApplyHelper() {
    }

}
