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

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.exceptions.XACMLException;
import org.glite.authz.pap.common.utils.xacml.DataType;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.common.utils.xacml.XACMLHelper;
import org.glite.authz.pap.distribution.DistributionConfigurationParser;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.glite.authz.pap.ui.wizard.AttributeWizard;
import org.glite.authz.pap.ui.wizard.UnsupportedAttributeException;
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

	public static void main(String[] args) throws ConfigurationException, UnsupportedAttributeException {
		
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
		
		DistributionModule distribution = DistributionModule.getInstance();
		distribution.start();
		pln("Going on...");
		for (int i=0; i<100000; i++) for (int j=0; j<1000000; j++);
		distribution.end();
		
		pln("Bye");
		
		System.exit(0);
		
		List<AttributeWizard> attributeList = new LinkedList<AttributeWizard>();
		for (int i=0; i<2; i++) {
			AttributeWizard entry = new AttributeWizard("fqan", "FQAN_" + i);
			attributeList.add(entry);
		}
		for (int i=0; i<2; i++) {
			AttributeWizard entry = new AttributeWizard("dn", "DN_" + i);
			attributeList.add(entry);
		}
		for (int i=0; i<2; i++) {
			AttributeWizard entry = new AttributeWizard("resource_uri", "RESOURCE_URI_" + i);
			attributeList.add(entry);
		}
		PolicyType policy = PolicyWizard.build("prova", attributeList, attributeList, EffectType.Deny);
		pln("Creata la policy");
		
		pln("Ora scrivo su file");
		
		PolicyHelper.getInstance().toFile("/tmp/pippo.xml", policy);
		
		pln("Stop");
		
		//System.exit(0);
		
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
