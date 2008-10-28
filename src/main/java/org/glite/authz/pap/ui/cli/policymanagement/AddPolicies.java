package org.glite.authz.pap.ui.cli.policymanagement;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class AddPolicies extends PolicyManagementCLI {

    private static final String USAGE = "<file> [[file] ...] [options]";
    private static final String[] commandNameValues = { "add-policy", "ap" };
    private static final String DESCRIPTION = "Add policies defined in the files.";
    private PolicyFileEncoder policyFileEncoder = new PolicyFileEncoder();
    
    public AddPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected void executeCommand(CommandLine commandLine) throws CLIException, ParseException, RemoteException {
        String[] args = commandLine.getArgs();
        
        if (args.length < 2)
            throw new ParseException("No input files defined.");
        
        for (int i=1; i<args.length; i++) {
            
            String fileName = args[i];
            
            try {
                addPolicy(fileName);
            } catch (EncodingException e) {
                System.out.println("Syntax error. Skipping file:" + fileName);
                System.out.println(e.getMessage());
            }
        }
        
        System.out.println("SUCCESS: policies has been added.");
    }
    
    private void addPolicy(String fileName) throws EncodingException, ParseException, RemoteException {
        File file = new File(fileName);
        
        if (!file.exists())
            throw new ParseException("File not found: " + file.getAbsolutePath());
        
        initOpenSAML();
        
        List<XACMLObject> policyList = policyFileEncoder.parse(file);
        
        for (XACMLObject xacmlObject:policyList) {
            
            if (xacmlObject instanceof PolicySetType)
                continue;
            PolicyWizard pw = new PolicyWizard((PolicyType) xacmlObject);
            
            System.out.println("Adding policy: ");
            System.out.println(pw.toFormattedString(15, 4));
            
            addPolicy(pw);
        }
        
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

}
