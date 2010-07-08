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

package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class RemoveObligation extends AbstractObligationManagementCommand {
	
	private static final String[] COMMAND_NAME_VALUES = { "remove-obligation", "ro" };
	private static final String DESCRIPTION = "Removes the obligation with id <obligationId> from the policy identified by <policyId>.";
	public RemoveObligation() {
		super(COMMAND_NAME_VALUES,USAGE,DESCRIPTION, LONG_DESCRIPTION);
	}

	@Override
	protected int executeCommand(CommandLine commandLine) throws CLIException,
			ParseException, RemoteException {
		
		
		parseCommandLine(commandLine);
				
		XACMLPolicyCLIUtils.initOpenSAMLAndAttributeWizard();
		
		if (xacmlPolicyMgmtClient.hasPolicySet(papAlias, policyId)){
			
			PolicySetType policySet = xacmlPolicyMgmtClient.getPolicySet(papAlias, policyId);
			
			if (!PolicySetHelper.hasObligationWithId(policySet, obligationId)){
				System.err.println("Obligation '"+obligationId+"' not defined for policy '"+policyId+"'.");
				return ExitStatus.FAILURE.ordinal();
			}
			
			if (removeObligationFromPolicySet(policySet)){
				
				if (!updatePolicySet(policySet))
					return ExitStatus.FAILURE.ordinal();
				
				return ExitStatus.SUCCESS.ordinal();
			
			}else{
				
				// Should never happen
				System.err.println("Obligation '"+obligationId+"' not defined for policy '"+policyId+"'.");
				return ExitStatus.FAILURE.ordinal();
			}
			
			
		}else if (xacmlPolicyMgmtClient.hasPolicy(papAlias, policyId)){
			
			PolicyType policy = xacmlPolicyMgmtClient.getPolicy(papAlias, policyId); 
			
			if (!PolicyHelper.hasObligationWithId(policy, obligationId)){
				System.err.println("Obligation '"+obligationId+"' not defined for policy '"+policyId+"'.");
				return ExitStatus.FAILURE.ordinal();
			}
			
			if (removeObligationFromPolicy(policy)){
				
				if (!updatePolicy(policy))
					return ExitStatus.FAILURE.ordinal();
				
				return ExitStatus.SUCCESS.ordinal();
			
			}else{
				
				// Should never happen
				System.err.println("Obligation '"+obligationId+"' not defined for policy '"+policyId+"'.");
				return ExitStatus.FAILURE.ordinal();
				
			}
			
		}
		
		System.err.println("No policyset or policy found for the given id '"+policyId+"'");
		return ExitStatus.FAILURE.ordinal();
	}


}
