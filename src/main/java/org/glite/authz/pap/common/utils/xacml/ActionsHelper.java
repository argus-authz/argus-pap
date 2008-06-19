package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xml.Configuration;

public class ActionsHelper extends XACMLHelper<ActionsType> {
	private static ActionsHelper instance = null;

	public static ActionsHelper getInstance() {
		if (instance == null) {
			instance = new ActionsHelper();
		}
		return instance;
	}

	private ActionsHelper() {
	}

	public static ActionsType buildAnyAction() {
		return (ActionsType) Configuration.getBuilderFactory().getBuilder(
				ActionsType.DEFAULT_ELEMENT_NAME).buildObject(
						ActionsType.DEFAULT_ELEMENT_NAME);
	}

}
