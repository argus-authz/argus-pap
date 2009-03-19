package org.glite.authz.pap.ui.cli.policymanagement;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
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
    private static final String DESCRIPTION = "Update the resource/action identified by <id> with the new resource/action "
            + "defined in <file>. In the case of update of a resource all the actions defined inside the new resource (the one inside <file>) "
            + "are ignored. This means that for a resource only obligations and the description can be updated. To remove, add, and "
            + "change the order of actions inside a resource use the appropriate commands.";
    private static final String USAGE = "<id> <file> [options]";
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

        PolicySetType repositoryPolicySet = xacmlPolicyMgmtClient.getPolicySet(alias, id);

        List<String> policyIdList = PolicySetHelper.getPolicyIdReferencesValues(repositoryPolicySet);
        List<String> policySetIdList = PolicySetHelper.getPolicySetIdReferencesValues(repositoryPolicySet);

        String repositoryVersion = repositoryPolicySet.getVersion();

        TypeStringUtils.releaseUnneededMemory(repositoryPolicySet);

        PolicySetType newPolicySet = policySetWizard.getXACML();

        newPolicySet.getPolicyIdReferences().clear();
        newPolicySet.getPolicySetIdReferences().clear();

        for (String idRef : policyIdList) {
            PolicySetHelper.addPolicyReference(newPolicySet, idRef);
        }
        for (String idRef : policySetIdList) {
            PolicySetHelper.addPolicySetReference(newPolicySet, idRef);
        }

        newPolicySet.setPolicySetId(id);
        newPolicySet.setVersion(repositoryVersion);
        PolicySetWizard.increaseVersion(newPolicySet);

        xacmlPolicyMgmtClient.updatePolicySet(alias, repositoryVersion, newPolicySet);

        return ExitStatus.SUCCESS.ordinal();
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create(OPT_PAPALIAS));
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException, RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length != 3) {
            throw new ParseException("Wrong number of arguments.");
        }

        if (commandLine.hasOption(OPT_PAPALIAS)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS);
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
