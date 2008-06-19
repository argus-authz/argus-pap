package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.FunctionType;
import org.opensaml.xml.Configuration;

public class FunctionHelper extends XACMLHelper<FunctionType> {

	private static FunctionHelper instance = null;

	public static FunctionType build(String functionId) {
		FunctionType function = (FunctionType) Configuration
				.getBuilderFactory().getBuilder(
						FunctionType.DEFAULT_ELEMENT_NAME).buildObject(
						FunctionType.DEFAULT_ELEMENT_NAME);
		function.setFunctionId(functionId);
		return function;
	}

	public static FunctionHelper getInstance() {
		if (instance == null) {
			instance = new FunctionHelper();
		}
		return instance;
	}

	private FunctionHelper() {
	}

}
