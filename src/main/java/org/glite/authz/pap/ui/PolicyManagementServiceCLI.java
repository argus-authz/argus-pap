package org.glite.authz.pap.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.utils.xacml.XMLObjectHelper;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.policymanagement.client.PolicyManagementServiceClientFactory;
import org.glite.authz.pap.policymanagement.client.PolicyManagementServicePortType;
import org.glite.authz.pap.ui.wizard.AttributeWizard;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;

public class PolicyManagementServiceCLI {
    private static final String POLICY_MGMT_SERVICE_URL = "https://localhost:8443/pap/services/PolicyManagementService";
    private static final PolicyManagementServicePortType policyMgmtClient;
    
    static {
        PolicyManagementServiceClientFactory policyMgmtFactory = PolicyManagementServiceClientFactory.getPolicyManagementServiceClientFactory();
        policyMgmtClient = policyMgmtFactory.createPolicyManagementServiceClient().getPolicyManagementServicePortType(POLICY_MGMT_SERVICE_URL);
    }
    
    public static void addPolicy(String[] args) throws ParseException {
        boolean argsDefinePolicy = false;
        
        if (args.length > 1) {
            argsDefinePolicy = true;
        } else if (args[0].contains("=".subSequence(0, 1))) {
            argsDefinePolicy = true;
        }
        
        List<PolicyWizard> policyWizardList;
        if (argsDefinePolicy) {
            policyWizardList = new LinkedList<PolicyWizard>();
            policyWizardList.add(getPolicyWizard(args));
        } else {
            InputStream inputStream;
            
            if (args.length == 1) {
                File file = new File(args[0]);
                try {
                    inputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found: " + file.getAbsolutePath());
                    return;
                }
            } else {
                inputStream = System.in;
            }
            try {
                policyWizardList = getPolicyWizardListFromInputStream(inputStream);
            } catch (EncodingException e) {
                System.out.println("Parsing error: " + e.getMessage());
                return;
            }
        }
        
        for (PolicyWizard policyW:policyWizardList) {
            try {
                String policyId = policyMgmtClient.storePolicy(policyW.getPolicyIdPrefix(), policyW.getPolicyType());
                System.out.println("Added policy: " + policyId);
            } catch (RemoteException e) {
                System.out.println("Comunication error: " + e.getMessage());
                return;
            }
        }
    }
    
    public static void list(boolean xacmlView) {
        
        List<PolicyType> policyList;
        try {
            policyList = policyMgmtClient.listPolicies();
        } catch (RemoteException e) {
            System.out.println("Comunication error: " + e.getMessage());
            return;
        }
        
        for (PolicyType policy:policyList) {
            PolicyWizard pw = new PolicyWizard(policy);
            if (xacmlView)
                System.out.println(XMLObjectHelper.toString(pw.getPolicyType()));
            else
                System.out.println(pw.toFormattedString());
        }
    }
    
    public static void removePolicy(String[] idArray) {
        for (String id:idArray) {
            System.out.println("Removing: " + id);
            try {
                policyMgmtClient.removePolicy(id);
            } catch (RemoteException e) {
                System.out.println("Comunication error: " + e.getMessage());
                return;
            }
            System.out.println("Removed policy: " + id);
        }
    }
    
    private PolicyManagementServiceCLI() { }
    
    private static PolicyWizard getPolicyWizard(String[] args) throws ParseException
    {
        List<String> targetList = new LinkedList<String>();
        List<String> exceptionsList = new LinkedList<String>();
        EffectType effect = EffectType.Deny;
        
        for (String s:args) {
            System.out.println("Analizing: " + s);
            if (s.startsWith("except=")) {
                exceptionsList.add(s.substring(s.indexOf('=')+1));
                System.out.println("    found: " + exceptionsList.get(exceptionsList.size()-1));
            } else if (s.startsWith("effect=")) {
                String value = s.substring(s.indexOf('=')+1);
                if ("allow".equals(value)) {
                    System.out.println("Effect=permit");
                    effect = EffectType.Permit;
                }
            } else if (s.contains("=".subSequence(0, 1))) {
                targetList.add(s);
            } else if (args.length == 1) {
                throw new ParseException("Unrecognized token: " + s);
            } else {
                targetList.add(s);
            }
            
        }
        
        List<AttributeWizard> targetWizardList = new LinkedList<AttributeWizard>();
        List<List<AttributeWizard>> exceptionWizardList = new LinkedList<List<AttributeWizard>>();
        
        targetWizardList = new LinkedList<AttributeWizard>();
        for (String s:targetList) {
            targetWizardList.add(new AttributeWizard(s));
        }
        
        exceptionWizardList = new LinkedList<List<AttributeWizard>>();
        for (String s:exceptionsList) {
            List<AttributeWizard> andExceptionsAttributeWizardList = new LinkedList<AttributeWizard>();
            String[] tokens = s.split(",");
            for (String token:tokens) {
                andExceptionsAttributeWizardList.add(new AttributeWizard(token));
            }
            exceptionWizardList.add(andExceptionsAttributeWizardList);
        }
        
        return new PolicyWizard(targetWizardList, exceptionWizardList, effect);
    }
    
    private static List<PolicyWizard> getPolicyWizardListFromInputStream(InputStream stream) throws EncodingException {
        PolicyFileEncoder encoder = new PolicyFileEncoder();
        List<PolicyWizard> policyWizardList = new LinkedList<PolicyWizard>();
        for (XACMLObject policy:encoder.parse(stream)) {
            if (policy instanceof PolicyType) {
                policyWizardList.add(new PolicyWizard((PolicyType) policy));
            }
        }
        return policyWizardList;
    }

}
