package org.glite.authz.pap.ui.cli.policymanagement;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.wizard.BlacklistPolicySet;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.ServiceClassPolicySet;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class AddPolicies extends PolicyManagementCLI {

    private static final String USAGE = "<file> [[file] ...] [options]";
    private static final String[] commandNameValues = { "add-policies-from-file", "ap" };
    private static final String DESCRIPTION = "Add policies defined in the given files.";
    private PolicyFileEncoder policyFileEncoder = new PolicyFileEncoder();
    
    public AddPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException, RemoteException {
        String[] args = commandLine.getArgs();
        
        if (args.length < 2)
            throw new ParseException("No input files defined.");
        
        for (int i=1; i<args.length; i++) {
            File file = new File(args[i]);
            if (!file.exists())
                throw new ParseException("File not found: " + file.getAbsolutePath());
        }
        
        boolean partialSuccess = false;
        boolean failure = false;
        
        for (int i=1; i<args.length; i++) {
            
            String fileName = args[i];
            
            try {
                
                boolean result = addPolicy(fileName);
                
                if (result == true) {
                    partialSuccess = true;
                }
                
            } catch (EncodingException e) {
                failure = true;
                System.out.println("Syntax error. Skipping file (no policies has been added):" + fileName);
                System.out.println(e.getMessage());
                continue;
            }
            
            if (verboseMode)
                System.out.println("Success: policies has been added from file " + fileName);
        }
        
        if (failure && !partialSuccess)
            return ExitStatus.FAILURE.ordinal();
        
        if (failure && partialSuccess)
            return ExitStatus.PARTIAL_SUCCESS.ordinal();
        
        return ExitStatus.SUCCESS.ordinal();
        
    }
    
    private boolean addPolicy(String fileName) throws EncodingException, RemoteException {
        
        boolean result = true;
        
        File file = new File(fileName);
        
        XACMLPolicyCLIUtils.initOpenSAML();
        
        List<XACMLObject> policyList = policyFileEncoder.parse(file);
        
        for (XACMLObject xacmlObject:policyList) {
            
            if (xacmlObject instanceof PolicySetType)
                continue;
            
            PolicyType policy = (PolicyType) xacmlObject;
            PolicyWizard policywizard = new PolicyWizard(policy);
            
            if (verboseMode) {
                System.out.print("Adding policy: ");
                System.out.println(policywizard.toFormattedString(0, 19));
            }
            
            String policySetId;
            
            if (policywizard.isBlacklistPolicy()) {
                policySetId = BlacklistPolicySet.POLICY_SET_ID;
            } else {
                policySetId = ServiceClassPolicySet.POLICY_SET_ID;
            }
            
            // the effective PolicyId is the one returned by the server
            String policyId = xacmlPolicyMgmtClient.addPolicy(policySetId, policywizard.getPolicyIdPrefix(), policy);
            
            if (policyId == null) {
                System.out.println("Error policy not added");
                result = false;
            }
            
            policy.setPolicyId(policyId);
        }
        return result;
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }
}
