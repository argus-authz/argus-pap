package test.repository;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.exceptions.XACMLException;
import org.glite.authz.pap.common.utils.xacml.DataType;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.common.utils.xacml.XACMLHelper;
import org.glite.authz.pap.distribution.DistributionConfigurationParser;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.distribution.RemotePAP;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.dao.PAPPolicySetDAO;
import org.glite.authz.pap.repository.dao.RootPolicySetDAO;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.glite.authz.pap.ui.wizard.WizardAttribute;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.ResourceMatchType;
import org.opensaml.xacml.policy.impl.*;
import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.impl.PolicyTypeImplBuilder;
import org.opensaml.xacml.policy.impl.SubjectMatchTypeImplBuilder;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLConfigurator;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;

public class TestRepository_ALB {
	
	private static PolicySetHelper policySetHelper;
	private static PolicyHelper policyHelper;
	private static final String papId = "Local";
	private static final String tempOutputDir = "/tmp/pap_output_dir";
	private static final String policyRepositoryFile = "/home/alb/pap/policy_repository.txt";

	public static void main(String[] args) throws ConfigurationException {
		
//		DistributionModule dm = DistributionModule.getInstance();
//		dm.start();
//		for (long j=0; j<10000; j++) {
//			for (long i=0; i<10000; i++) {}
//		}
//		pln("Stopping");
//		dm.end();
//		pln("Bye");
//		System.exit(0);
		
		
//		RepositoryManager rm = RepositoryManager.getInstance();
//		
//		DistributionConfigurationParser parseDistr = DistributionConfigurationParser.getInstance();
//		pln("Reading distribution configuration...");
//		List<RemotePAP> remotePAPList = parseDistr.parse(new File("/tmp/remote_pap_list.txt"));
//		if (remotePAPList.isEmpty()) {
//			pln("No remote PAP defined.");
//		} else {
//			for (RemotePAP pap:remotePAPList) {
//				pln("Remote PAP: " + pap.getDn());
//			}
//		}
//		
//		//rm.start();
//		
//		pln("Game over");
//		
//		System.exit(0);
//		
		
		
		pln("Start");
		
		// Initialize the library
		
		PAPConfiguration.bootstrap();
		
		
		List<WizardAttribute> attributeList = new LinkedList<WizardAttribute>();
		for (int i=0; i<2; i++) {
			WizardAttribute entry = new WizardAttribute(WizardAttribute.Type.SUBJECT_FQAN, "FQAN_" + i);
			attributeList.add(entry);
		}
		for (int i=0; i<2; i++) {
			WizardAttribute entry = new WizardAttribute(WizardAttribute.Type.SUBJECT_DN, "DN_" + i);
			attributeList.add(entry);
		}
		for (int i=0; i<2; i++) {
			WizardAttribute entry = new WizardAttribute(WizardAttribute.Type.RESOURCE_RESOURCE_URI, "RESOURCE_URI_" + i);
			attributeList.add(entry);
		}
		PolicyType policy = PolicyWizard.build(attributeList, attributeList, EffectType.Deny);
		pln("Creata la policy");
		
		pln("Ora scrivo su file");
		
		PolicyHelper.getInstance().toFile("/tmp/pippo.xml", policy);
		
		pln("Stop");
		
		//System.exit(0);
		
		policySetHelper = PolicySetHelper.getInstance();
		policyHelper = PolicyHelper.getInstance();
		DAOFactory daoFactory = DAOFactory.getDAOFactory();
		RootPolicySetDAO rootPolicySetDAO = daoFactory.getRootPolicySetDAO();
		
		if (!rootPolicySetDAO.exists()) {
				rootPolicySetDAO.create();
		}

		PAPPolicySetDAO papDAO = daoFactory.getPapDAO();
		// Create a PAP PolicySet
		PolicySetType localPAPPolicySet = PolicySetHelper.build(papId, PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
		if (!papDAO.exists(papId)) {
			papDAO.add(localPAPPolicySet);
		}
		
		// Insert PolicySet in the PAP
		PolicySetType examplePolicySet = PolicySetHelper.build("example_policyset_01", PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
		PolicySetDAO policySetDAO = daoFactory.getPolicySetDAO();
		policySetDAO.store(papId, examplePolicySet);
		
		// Insert Policy in the PAP
		PolicyDAO policyDAO = daoFactory.getPolicyDAO();
		PolicyType examplePolicy;
		for (int i=0; i<10 ;i++) {
			examplePolicy = PolicyHelper.build("example_" + i, PolicyHelper.RULE_COMBALG_DENY_OVERRIDS);
			policyDAO.store(papId, examplePolicy);
			PolicySetHelper.addPolicyReference(examplePolicySet, examplePolicy.getPolicyId());
		}
		
		policySetDAO.update(papId, examplePolicySet);
		
		File outputDir = new File(tempOutputDir);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		List<XACMLObject> list = rootPolicySetDAO.getTreeAsList();
		for (XACMLObject elem:list) {
			if (elem instanceof PolicySetType) {
				PolicySetType p = (PolicySetType) elem;
				String fileName = tempOutputDir + "/PolicySet_" + p.getPolicySetId() + ".xml";
				policySetHelper.toFile(fileName, p);
			} else if (elem instanceof PolicyType) {
				PolicyType p = (PolicyType) elem;
				String fileName = tempOutputDir + "/Policy_" + p.getPolicyId() + ".xml";
				policyHelper.toFile(fileName, p);
			}
		}
		
		pln("Reading PolicyRepository init file...");
		
		PolicyFileEncoder  penc = new PolicyFileEncoder();
		
		File prFile = new File(policyRepositoryFile);
		String policies = "";
		try {
			policies = penc.parse(prFile);
		} catch (EncodingException e) {
			pln("Error: encoding error from file: " + prFile.getAbsolutePath());
			e.printStackTrace();
		}
		
		pln("PolicyRepository read... here it is:");
		pln(policies);
		pln("Game over");
	}
	
	private static void pln(String s) {
		System.out.println(s);
	}
}
