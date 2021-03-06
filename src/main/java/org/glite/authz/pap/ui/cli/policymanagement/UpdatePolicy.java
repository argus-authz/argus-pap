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

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.XACMLWizard;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class UpdatePolicy extends PolicyManagementCLI {

    private static final String[] commandNameValues = { "update-policy-from-file", "upf" };
    private static final String DESCRIPTION = "Update the resource/action identified by <id> with the new resource/action "
            + "defined in <file>.";
    private static final String USAGE = "[options] <id> <file>";
    private PolicyFileEncoder policyFileEncoder = new PolicyFileEncoder();

    private String alias = null;

    public UpdatePolicy() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    private int updatePolicy(String id, PolicyWizard policyWizard) throws RemoteException {

        if (!xacmlPolicyMgmtClient.hasPolicy(alias, id)) {
            System.out.println("Error: action id \"" + id + "\" does not exists.");
            return ExitStatus.FAILURE.ordinal();
        }

        PolicyType oldPolicy = xacmlPolicyMgmtClient.getPolicy(alias, id);

        policyWizard.setPolicyId(id);
        policyWizard.increaseVersion();

        boolean success = xacmlPolicyMgmtClient.updatePolicy(alias, oldPolicy.getVersion(), policyWizard.getXACML());

        if (!success) {
            return ExitStatus.FAILURE.ordinal();
        }

        return ExitStatus.SUCCESS.ordinal();
    }

    private int updatePolicySet(String id, PolicySetWizard policySetWizard) throws RemoteException {

        if (!xacmlPolicyMgmtClient.hasPolicySet(alias, id)) {
            System.out.println("Error: resource id \"" + id + "\" does not exists.");
            return ExitStatus.FAILURE.ordinal();
        }

        List<PolicyWizard> policyWizardList = policySetWizard.getPolicyWizardList();
        
        PolicySetType repositoryPolicySet = xacmlPolicyMgmtClient.getPolicySet(alias, id);

        String repositoryVersion = repositoryPolicySet.getVersion();

        TypeStringUtils.releaseUnneededMemory(repositoryPolicySet);

        PolicySetType policySet = policySetWizard.getXACML();

        policySet.getPolicyIdReferences().clear();
        policySet.getPolicySetIdReferences().clear();

        policySet.setPolicySetId(id);
        policySet.setVersion(repositoryVersion);
        PolicySetWizard.increaseVersion(policySet);

        xacmlPolicyMgmtClient.updatePolicySet(alias, repositoryVersion, policySet);
        
        TypeStringUtils.releaseUnneededMemory(policySetWizard);
        TypeStringUtils.releaseUnneededMemory(policySet);
        
        // add actions
        int size = policyWizardList.size();
        PolicyType[] policyArray = new PolicyType[size];
        String[] idPrefixArray = new String[size];

        for (int i = 0; i < size; i++) {
            PolicyWizard policyWizard = policySetWizard.getPolicyWizardList().get(i);
            policyArray[i] = policyWizard.getXACML();
            idPrefixArray[i] = policyWizard.getPolicyIdPrefix();
            TypeStringUtils.releaseUnneededMemory(policyWizard);
        }

        xacmlPolicyMgmtClient.addPolicies(alias, 0, id, idPrefixArray, policyArray);
        
        highlevelPolicyMgmtClient.purge(alias, true, false, false, false);
        
        return ExitStatus.SUCCESS.ordinal();
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException, RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length != 3) {
            throw new ParseException("Wrong number of arguments.");
        }

        if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS_LONG);
        }

        String id = args[1];
        String fileName = args[2];

        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Error: file \"" + file.getAbsolutePath() + "\" does not exists.");
            return ExitStatus.FAILURE.ordinal();
        }

        XACMLPolicyCLIUtils.initOpenSAMLAndAttributeWizard();

        List<XACMLWizard> wizardList = new ArrayList<XACMLWizard>(0);

        try {

            wizardList = policyFileEncoder.parse(file);

        } catch (EncodingException e) {
            System.out.println("Syntax error in file: " + fileName);
            System.out.println(e.getMessage());
            return ExitStatus.FAILURE.ordinal();
        }

        if (wizardList.size() == 0) {
            System.out.println("Error: no policies has been defined in file \"" + fileName + "\"");
            return ExitStatus.FAILURE.ordinal();
        }

        if (wizardList.size() > 1) {
            System.out.println("Error: more than one element has been defined in file \"" + fileName + "\"");
            return ExitStatus.FAILURE.ordinal();
        }

        XACMLWizard xacmlWizard = wizardList.get(0);

        int status;

        if (xacmlWizard instanceof PolicySetWizard) {
            status = updatePolicySet(id, (PolicySetWizard) xacmlWizard);
        } else {
            status = updatePolicy(id, (PolicyWizard) xacmlWizard);
        }

        if (verboseMode) {
            if (status == ExitStatus.SUCCESS.ordinal()) {
                System.out.println("Success: policy has been updated.");
            }
        }

        return status;
    }

}
