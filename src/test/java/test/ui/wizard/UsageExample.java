package test.ui.wizard;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.ui.wizard.AttributeWizard;
import org.glite.authz.pap.ui.wizard.BlacklistPolicy;
import org.glite.authz.pap.ui.wizard.BlacklistPolicySet;
import org.glite.authz.pap.ui.wizard.LocalPAPPolicySet;
import org.glite.authz.pap.ui.wizard.ServiceClassPolicySet;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xml.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsageExample {
	final static Logger log = LoggerFactory.getLogger( UsageExample.class );

	public static void main(String[] args) throws ConfigurationException {
		
		PAPConfiguration.bootstrap();
		
		log.info("Start");
		List<XACMLObject> resultList = new LinkedList<XACMLObject>();
		
		PolicySetType localPAPPolicySet = LocalPAPPolicySet.build();
		resultList.add(localPAPPolicySet);
		
		PolicySetType blacklistPolicySet = BlacklistPolicySet.build();
		resultList.add(blacklistPolicySet);

		// Blacklist stuff example
		List<AttributeWizard> targetAttributeList = new LinkedList<AttributeWizard>();
		targetAttributeList.add(new AttributeWizard(AttributeWizard.Type.SUBJECT_FQAN, "FQAN_value"));
		
		List<AttributeWizard> exceptionsAttributeList = new LinkedList<AttributeWizard>();
		exceptionsAttributeList.add(new AttributeWizard(AttributeWizard.Type.SUBJECT_DN, "DN_value"));
		
		PolicyType blacklistPolicy = BlacklistPolicy.build(targetAttributeList, exceptionsAttributeList);
		
		// add the whole policy (in this case do not add the policy to the "resultList")
		PolicySetHelper.addPolicy(blacklistPolicySet, blacklistPolicy);
		
		// ...or add a reference
		exceptionsAttributeList.clear();
		blacklistPolicy = BlacklistPolicy.build(targetAttributeList, exceptionsAttributeList);
		PolicySetHelper.addPolicyReference(blacklistPolicySet, blacklistPolicy.getPolicyId());
		resultList.add(blacklistPolicy);
		
		// ServiceClass stuff example
		PolicySetType serviceClassPolicySet = ServiceClassPolicySet.build();
		resultList.add(serviceClassPolicySet);
		
		// Add references of Blacklist and ServiceClass policy sets to the local PAP policy set
		PolicySetHelper.addPolicySetReference(localPAPPolicySet, blacklistPolicySet.getPolicySetId());
		PolicySetHelper.addPolicySetReference(localPAPPolicySet, serviceClassPolicySet.getPolicySetId());
		
		log.info("Printing the result list:");
		
		for (XACMLObject xacmlObject:resultList) {
			if (xacmlObject instanceof PolicySetType) {
				log.info("PolicySet: " + ((PolicySetType)xacmlObject).getPolicySetId());
//				PolicySetHelper.getInstance().toFile("/tmp/PolicySet_" + ((PolicySetType)xacmlObject).getPolicySetId() + ".xml", (PolicySetType)xacmlObject);
			} else if (xacmlObject instanceof PolicyType) {
				log.info("Policy   : " + ((PolicyType)xacmlObject).getPolicyId());
//				PolicyHelper.getInstance().toFile("/tmp/Policy_" + ((PolicyType)xacmlObject).getPolicyId() + ".xml", (PolicyType)xacmlObject);
			}
		}
		
		log.info("Done");
	}
}
