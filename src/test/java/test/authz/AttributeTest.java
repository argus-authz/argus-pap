/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.authz;

import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.ui.cli.policymanagement.XACMLPolicyCLIUtils;

import junit.framework.TestCase;

public class AttributeTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
	super.setUp();

	System.setProperty("PAP_HOME", "src/test/resources");

	XACMLPolicyCLIUtils.initAttributeWizard();
    }

    @Override
    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void testEmptySubject() {
	String lValue = "subject=";
	try {
	    new AttributeWizard(lValue);
	} catch (Exception e) {
	    assertTrue(e instanceof PAPException);
	}
    }

    public void testSubject() {
	String lValue = "subject=CN=Test0, O=IGI, C=IT";
	try {
	    AttributeWizard lAttribute = new AttributeWizard(lValue);
	    assertTrue(lAttribute != null);
	} catch (Exception e) {
	    fail();
	}
    }
}
