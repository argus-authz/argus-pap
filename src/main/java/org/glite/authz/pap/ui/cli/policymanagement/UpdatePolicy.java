package org.glite.authz.pap.ui.cli.policymanagement;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.XACMLWizard;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class UpdatePolicy extends PolicyManagementCLI {

    private static final String[] commandNameValues = { "update-policy-from-file", "up" };
    private static final String DESCRIPTION = "Update the policy identified by \"id\" with the new policy "
            + "defined in \"file\"";
    private static final String USAGE = "<id> <file> [options]";
    private PolicyFileEncoder policyFileEncoder = new PolicyFileEncoder();

    public UpdatePolicy() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    private int updatePolicy(String id, PolicyWizard policyWizard) throws RemoteException {

        policyWizard.setPolicyId(id);
        policyWizard.increaseVersion();

        boolean success = xacmlPolicyMgmtClient.updatePolicy(policyWizard.getXACML());

        if (!success) {
            return ExitStatus.FAILURE.ordinal();
        }

        return ExitStatus.SUCCESS.ordinal();
    }

    private int updatePolicySet(String id, PolicySetWizard policySetWizard) throws RemoteException {

        if (!xacmlPolicyMgmtClient.hasPolicySet(id)) {
            System.out.println("Error: resource id \"" + id + "\" does not exists.");
            return ExitStatus.FAILURE.ordinal();
        }

        PolicySetType policySet = xacmlPolicyMgmtClient.getPolicySet(id);

        List<PolicyType> repoPolicyList = new LinkedList<PolicyType>();
        for (String policyId : PolicySetHelper.getPolicyIdReferencesValues(policySet)) {
            repoPolicyList.add(xacmlPolicyMgmtClient.getPolicy(policyId));
        }

        List<PolicyWizard> policyWizardList = policySetWizard.getPolicyWizardList();

        if (policyWizardList.size() != repoPolicyList.size()) {
            System.out.println("Error: the set of actions is not the same, only order can be changed with the update operation");
            return ExitStatus.FAILURE.ordinal();
        }

        for (PolicyWizard policyWizard : policyWizardList) {

            boolean foundToBeEquivalent = false;

            for (PolicyType policy : repoPolicyList) {
                if (policyWizard.isEquivalent(policy)) {
                    if (!PolicySetHelper.changePolicyReferenceValue(policySetWizard.getXACML(), policyWizard.getPolicyId(), policy
                            .getPolicyId())) {
                        break;
                    }
                    policyWizard.setPolicyId(policy.getPolicyId());
                    foundToBeEquivalent = true;
                    break;
                }
            }

            if (!foundToBeEquivalent) {
                System.out
                        .println("Error: the set of actions is not the same, only order can be changed with the update operation");
                return ExitStatus.FAILURE.ordinal();
            }
        }
        
        policySetWizard.setPolicySetId(id);
        policySetWizard.increaseVersion();
        xacmlPolicyMgmtClient.updatePolicySet(policySetWizard.getXACML());

        return ExitStatus.SUCCESS.ordinal();
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException, RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length != 3) {
            throw new ParseException("Wrong number of arguments.");
        }

        String id = args[1];
        String fileName = args[2];

        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Error: file \"" + file.getAbsolutePath() + "\" does not exists.");
            return ExitStatus.FAILURE.ordinal();
        }

        XACMLPolicyCLIUtils.initOpenSAML();

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
