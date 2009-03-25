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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddPolicies extends PolicyManagementCLI {

    private static final Logger log = LoggerFactory.getLogger(AddPolicies.class);
    private static String OPT_PIVOT = "pivot";
    private static String OPT_PIVOT_LONG = "pivot";
    private static String OPT_PIVOT_DESCRIPTION = "insert after <pivotId> (by default the insertion is before <pivotId>)";

    private static final String[] commandNameValues = { "add-policies-from-file", "ap" };
    private static final String DESCRIPTION = "Add policies defined in the given file.\n"
            + "[targetId]   resource id in which insert actions.\n"
            + "<file>       define a set of resource elements or a set of action elements.\n";
    private static final String LONG_DESCRIPTION = "If <file> defines a set of resource elements \"targetId\" must not "
            + "be provided, otherwise if only action elements are defined \"targetId\" indentifies the resource element "
            + "in which insert the given action elements. If option --" + OPT_PIVOT_LONG + " is not specified all the "
            + "elements are inserted in the last available position, otherwise they are inserted before \"pivotId\" "
            + "(or after \"pivotId\" if option --" + OPT_MOVEAFTER_LONG + " is set).";
    private static final String USAGE = "[options] <file> [targetId]";
    private PolicyFileEncoder policyFileEncoder = new PolicyFileEncoder();
    private List<XACMLWizard> xacmlWizardList;
    private String targetId = null;
    private String pivotId = null;
    private String alias = null;
    private boolean moveAfter = false;

    public AddPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, LONG_DESCRIPTION);
    }

    private boolean addResources() throws RemoteException {

        for (XACMLWizard xacmlWizard : xacmlWizardList) {
            if (!(xacmlWizard instanceof PolicySetWizard)) {
                System.out.println(String.format("Error: found action element (%s). Only highlevel \"resource\" elements are allowed.",
                                                 ((PolicyWizard) xacmlWizard).getTagAndValue()));
                return false;
            }
        }

        if (targetId != null) {
            System.out.println("Error cannot use \"targetId\" to insert resource elements.");
            return false;
        }

        int position = -1;

        if (pivotId != null) {
            PolicySetType rootPolicySet = xacmlPolicyMgmtClient.getRootPolicySet(null);
            position = PolicySetHelper.getPolicySetIdReferenceIndex(rootPolicySet, pivotId);
            if (position == -1) {
                System.out.println("Pivot id \"" + pivotId + "\" not found (or not a resource id).");
                return false;
            }
            if (moveAfter) {
                position++;
            }
        }

        boolean result = true;

        for (XACMLWizard xacmlWizard : xacmlWizardList) {

            PolicySetWizard policySetWizard = (PolicySetWizard) xacmlWizard;

            PolicySetType policySet = policySetWizard.getXACML();

            policySet.getPolicyIdReferences().clear();

            policySetWizard.releaseDOM();

            String policySetId = xacmlPolicyMgmtClient.addPolicySet(alias, position, policySet);

            if (position != -1) {
                position++;
            }

            if (policySetId == null) {
                System.out.println(String.format("Error policy set not added: %s (id=%s). Skipping all the policies defined inside.",
                                                 policySetWizard.getTagAndValue(),
                                                 policySetWizard.getPolicySetId()));
                result = false;
                continue;
            }

            if (verboseMode) {
                System.out.println(String.format("Added policy set: %s (id=%s)", policySetWizard.getTagAndValue(), policySetId));
            }

            int size = policySetWizard.getPolicyWizardList().size();
            PolicyType[] policyArray = new PolicyType[size];
            String[] idPrefixArray = new String[size];
            String[] tagAndValueArray = new String[size];

            for (int i = 0; i < size; i++) {
                PolicyWizard policyWizard = policySetWizard.getPolicyWizardList().get(i);
                policyArray[i] = policyWizard.getXACML();
                idPrefixArray[i] = policyWizard.getPolicyIdPrefix();
                tagAndValueArray[i] = policyWizard.getTagAndValue();
                policyWizard.releaseChildrenDOM();
                policyWizard.releaseDOM();
                policyWizard = null;
            }

            String[] policyIdArray = xacmlPolicyMgmtClient.addPolicies(alias, 0, policySetId, idPrefixArray, policyArray);

            for (int i = 0; i < size; i++) {
                String policyId = policyIdArray[i];
                String tagAndValue = tagAndValueArray[i];

                if (policyId == null) {
                    System.out.println(String.format("Error policy not added: %s", tagAndValue));
                    result = false;
                    continue;
                }

                if (verboseMode) {
                    System.out.println(String.format("Added policy: %s (id=%s)", tagAndValue, policyId));
                }
            }

            if (verboseMode) {
                System.out.println();
            }
        }
        return result;
    }

    private boolean addActions() throws RemoteException {

        List<PolicyWizard> policyWizardList = new ArrayList<PolicyWizard>(xacmlWizardList.size());

        for (XACMLWizard xacmlWizard : xacmlWizardList) {
            if (!(xacmlWizard instanceof PolicyWizard)) {
                System.out.println(String.format("Error: found resource element (%s). Only \"action\" highlevel elements are allowed.",
                                                 ((PolicySetWizard) xacmlWizard).getTagAndValue()));
                return false;
            }
            policyWizardList.add((PolicyWizard) xacmlWizard);
        }

        if (targetId == null) {
            System.out.println("Error \"targetId\" is needed to insert action elements");
            return false;
        }

        PolicySetType targetolicySet = xacmlPolicyMgmtClient.getPolicySet(alias, targetId);

        int position = -1;

        if (pivotId != null) {
            position = PolicySetHelper.getPolicyIdReferenceIndex(targetolicySet, pivotId);
            TypeStringUtils.releaseUnneededMemory(targetolicySet);
            if (position == -1) {
                System.out.println("Pivot id \"" + pivotId + "\" not found inside resource id \"" + targetId + "\".");
                return false;
            }
            if (moveAfter) {
                position++;
            }
        }

        boolean result = true;

        int size = xacmlWizardList.size();
        PolicyType[] policyArray = new PolicyType[size];
        String[] idPrefixArray = new String[size];
        String[] tagAndValueArray = new String[size];

        for (int i = 0; i < size; i++) {
            PolicyWizard policyWizard = policyWizardList.get(i);
            policyArray[i] = policyWizard.getXACML();
            idPrefixArray[i] = policyWizard.getPolicyIdPrefix();
            tagAndValueArray[i] = policyWizard.getTagAndValue();
            TypeStringUtils.releaseUnneededMemory(policyWizard);
            policyWizard = null;
        }

        log.debug("Inserting actions into position: " + position);

        String[] policyIdArray = xacmlPolicyMgmtClient.addPolicies(alias, position, targetId, idPrefixArray, policyArray);

        for (int i = 0; i < size; i++) {
            String policyId = policyIdArray[i];
            String tagAndValue = tagAndValueArray[i];

            if (policyId == null) {
                System.out.println(String.format("Error policy not added: %s", tagAndValue));
                result = false;
                continue;
            }

            if (verboseMode) {
                System.out.println(String.format("Added policy: %s (id=%s)", tagAndValue, policyId));
            }
        }

        if (verboseMode) {
            System.out.println();
        }
        return result;
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_MOVEAFTER_DESCRIPTION)
                                       .withLongOpt(OPT_MOVEAFTER_LONG)
                                       .create(OPT_MOVEAFTER));
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PIVOT_DESCRIPTION)
                                       .withLongOpt(OPT_PIVOT_LONG)
                                       .create(OPT_PIVOT));
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create(OPT_PAPALIAS));
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException, RemoteException {
        String[] args = commandLine.getArgs();

        if ((args.length < 2) || (args.length > 3)) {
            throw new ParseException("Wrong number of arguments.");
        }

        if (commandLine.hasOption(OPT_PAPALIAS)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS);
        }

        if (commandLine.hasOption(OPT_PIVOT)) {
            pivotId = commandLine.getOptionValue(OPT_PIVOT);
        }

        if ((args.length == 3)) {
            targetId = args[2];
        }

        if (commandLine.hasOption(OPT_MOVEAFTER)) {
            moveAfter = true;
        }

        log.debug("args.lengh=" + args.length);
        log.debug("targetId=" + targetId);
        log.debug("pivotId=" + pivotId);
        log.debug("moveAfter=" + moveAfter);

        File file = new File(args[1]);

        XACMLPolicyCLIUtils.initOpenSAMLAndAttributeWizard();

        try {
            xacmlWizardList = policyFileEncoder.parse(file);
        } catch (EncodingException e) {
            System.out.println("Syntax error no policies has been added from file:" + file.getAbsolutePath());
            System.out.println("Reason:");
            System.out.println(e.getMessage());
            return ExitStatus.FAILURE.ordinal();
        }

        if (xacmlWizardList.isEmpty()) {
            System.out.println("No policies defined in the give file");
            return ExitStatus.FAILURE.ordinal();
        }

        boolean result;

        if (xacmlWizardList.get(0) instanceof PolicySetWizard) {
            result = addResources();
        } else {
            result = addActions();
        }

        if (result == true) {
            if (verboseMode) {
                System.out.println("Success: policies has been added from file " + file.getAbsolutePath());
            }
        } else {
            return ExitStatus.FAILURE.ordinal();
        }
        return ExitStatus.SUCCESS.ordinal();
    }
}
