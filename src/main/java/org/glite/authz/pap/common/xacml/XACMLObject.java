package org.glite.authz.pap.common.xacml;

import org.w3c.dom.Node;

public interface XACMLObject {
	public Node getDOM();
	public boolean isPolicy();
	public boolean isPolicyReference();
	public boolean isPolicySet();
	public boolean isPolicySetReference();
	public boolean isReference();
}
