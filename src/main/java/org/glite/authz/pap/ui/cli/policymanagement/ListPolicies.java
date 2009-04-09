package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListPolicies extends PolicyManagementCLI {

    private static final String[] commandNameValues = { "list-policies", "lp" };
    private static final String DESCRIPTION = "List policies. If no options are specified then the policies of the default pap are listed.";
    private static final Logger log = LoggerFactory.getLogger(ListPolicies.class);
    private static final String USAGE = "[options]";

    public ListPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    protected boolean listPolicies(String papAlias, boolean showIds, boolean showRuleId, boolean xacmlOutput)
            throws RemoteException {

        boolean somethingHasBeenSelected = false;

        PolicySetType[] policySetArray;

        policySetArray = xacmlPolicyMgmtClient.listPolicySets(papAlias);

        if (policySetArray.length == 0) {
            throw new CLIException("Error: the repository seems to be corrupted, no policy sets have been found");
        }

        PolicyType[] policyArray;

        policyArray = xacmlPolicyMgmtClient.listPolicies(papAlias);

        List<PolicyWizard> policyWizardList = new ArrayList<PolicyWizard>(policyArray.length);

        for (PolicyType policy : policyArray) {
            PolicyWizard policyWizard = new PolicyWizard(policy);
            policyWizardList.add(policyWizard);
            policyWizard.releaseChildrenDOM();
            policyWizard.releaseDOM();
        }

        policyArray = null;

        PolicySetType localRootPolicySet = policySetArray[0];

        for (String policySetId : PolicySetHelper.getPolicySetIdReferencesValues(localRootPolicySet)) {

            PolicySetType policySet = null;

            for (PolicySetType policySetElem : policySetArray) {
                if (policySetId.equals(policySetElem.getPolicySetId())) {
                    policySet = policySetElem;
                    break;
                }
            }

            if (policySet == null) {
                throw new CLIException("Error: the repository seems to be corrupted, policy set not found: " + policySetId);
            }

            try {
                PolicySetWizard policySetWizard = new PolicySetWizard(policySet, policyWizardList, null);
                System.out.println();

                if (xacmlOutput) {
                    System.out.println(policySetWizard.toXACMLString());
                } else {
                    System.out.println(policySetWizard.toFormattedString(showIds, showRuleId));
                }

            } catch (UnsupportedPolicySetWizardException e) {
                log.error("Unsupported Policy/PolicySet", e);
                System.out.println("id=" + policySetId + ": " + GENERIC_XACML_ERROR_MESSAGE);
            }

            somethingHasBeenSelected = true;
        }

        return somethingHasBeenSelected;
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_SHOW_XACML_DESCRIPTION)
                                       .withLongOpt(OPT_SHOW_XACML_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_SHOW_RA_IDS_DESCRIPTION)
                                       .withLongOpt(OPT_SHOW_IDS_LONG)
                                       .create(OPT_SHOW_RA_IDS));
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_SHOW_ALL_IDS_DESCRIPTION)
                                       .withLongOpt(OPT_SHOW_ALL_IDS_LONG)
                                       .create(OPT_SHOW_ALL_IDS));
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_ALLPAPS_DESCRIPTION)
                                       .withLongOpt(OPT_ALLPAPS_LONG)
                                       .create(OPT_ALLPAPS));
        options.addOption(OptionBuilder.hasArgs()
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        boolean xacmlOutput = false;
        boolean showIds = false;
        boolean showRulesId = false;

        if (commandLine.hasOption(OPT_SHOW_XACML_LONG)) {
            xacmlOutput = true;
        }

        if (commandLine.hasOption(OPT_SHOW_RA_IDS)) {
            showIds = true;
        }

        if (commandLine.hasOption(OPT_SHOW_ALL_IDS_LONG)) {
            showRulesId = true;
            showIds = true;
        }

        String[] papAliasArray = null;
        String[] papInfoArray = null;

        if (commandLine.hasOption(OPT_ALLPAPS)) {
            
            Pap[] papArray = papMgmtClient.getAllPaps();
            papAliasArray = new String[papArray.length];
            for (int i = 0; i < papArray.length; i++) {
                papAliasArray[i] = papArray[i].getAlias();
            }
            papInfoArray = getPAPInfoArray(papAliasArray, papArray);
            
        } else if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {
            
            papAliasArray = commandLine.getOptionValues(OPT_PAPALIAS_LONG);
            papInfoArray = getPAPInfoArray(papAliasArray, null);
            
        } else {
            
            Pap pap = Pap.makeDefaultPAP();

            Pap[] papArray = new Pap[1];
            papArray[0] = pap;

            papAliasArray = new String[1];
            papAliasArray[0] = pap.getAlias();

            papInfoArray = getPAPInfoArray(papAliasArray, papArray);
        }

        XACMLPolicyCLIUtils.initOpenSAMLAndAttributeWizard();

        for (int i = 0; i < papAliasArray.length; i++) {

            System.out.println();
            System.out.println(papInfoArray[i]);

            boolean policiesFound = listPolicies(papAliasArray[i], showIds, showRulesId, xacmlOutput);

            if (!policiesFound) {
                printOutputMessage("No policies has been found.");
            }
        }

        return ExitStatus.SUCCESS.ordinal();
    }

    private String[] getPAPInfoArray(String[] papAliasArray, Pap[] papArray) throws RemoteException {

        int size = papAliasArray.length;
        String[] papInfoArray = new String[size];

        for (int i = 0; i < size; i++) {

            String alias = papAliasArray[i];

            Pap pap;

            if (papArray != null) {
                pap = papArray[i];
            } else {
                pap = papMgmtClient.getPap(alias);
            }

            if (pap.isLocal()) {
                papInfoArray[i] = String.format("%s (local):", pap.getAlias());
            } else {
                papInfoArray[i] = String.format("%s (%s:%s):", pap.getAlias(), pap.getHostname(), pap.getPort());
            }
        }
        return papInfoArray;
    }
}
