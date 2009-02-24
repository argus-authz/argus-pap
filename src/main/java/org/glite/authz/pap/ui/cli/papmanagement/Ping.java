package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.policymanagement.XACMLPolicyCLIUtils;
import org.opensaml.xacml.policy.EffectType;

public class Ping extends PAPManagementCLI {
	
	private static final String USAGE = "[options]";
    private static final String[] commandNameValues = { "ping" };
    private static final String DESCRIPTION = "Ping a PAP (default endpoint is: " + 
    DEFAULT_SERVICE_URL + ").";

	public Ping() {
		super(commandNameValues, USAGE, DESCRIPTION, null);
	}

	@Override
	protected int executeCommand(CommandLine commandLine)
			throws CLIException, ParseException, RemoteException {
	    
	    XACMLPolicyCLIUtils.initOpenSAML();
	    
	    AttributeWizard attribute = new AttributeWizard("resource_uri", "pippo");
	    
	    PolicySetWizard psw = new PolicySetWizard(attribute);
	    
	    System.out.println("---------------");
	    System.out.println(psw.toFormattedString(true));
	    System.out.println("---------------");
	    
	    //System.out.println(XMLObjectHelper.toString(psw.getXACML()));

	    List<AttributeWizard> ruleTarget1 = new LinkedList<AttributeWizard>();
	    ruleTarget1.add(new AttributeWizard("dn", "/DN=test_00"));
	    
	    List<AttributeWizard> ruleTarget2 = new LinkedList<AttributeWizard>();
        ruleTarget2.add(new AttributeWizard("dn", "/DN=test_02"));
	    
	    PolicyWizard pw = new PolicyWizard(new AttributeWizard("action", "job_submission"));
	    pw.addRule(ruleTarget1, EffectType.Deny);
	    pw.addRule(ruleTarget2, EffectType.Permit);
	    
	    System.out.println(pw.toFormattedString(true));
	    
	    System.out.println("---------------");
	    psw.addPolicy(pw);
	    System.out.println(psw.toFormattedString(true));
	    
	    return ExitStatus.SUCCESS.ordinal();
		
//		String[] args = commandLine.getArgs();
//		
//		if (args.length > 1)
//			throw new ParseException("Wrong number of arguments");
//		
//		String papVersion = papMgmtClient.ping();
//		
//		System.out.println("PAP successfully contacted: version=" + papVersion);
//		
//		return ExitStatus.SUCCESS.ordinal();
  	}

	@Override
	protected Options defineCommandOptions() {
		return null;
	}

}
