package test.alb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.ui.wizard.AttributeWizard;
import org.glite.authz.pap.ui.wizard.BlacklistPolicy;
import org.glite.authz.pap.ui.wizard.BlacklistPolicySet;
import org.glite.authz.pap.ui.wizard.LocalPAPPolicySet;
import org.glite.authz.pap.ui.wizard.PolicySetWizard;
import org.glite.authz.pap.ui.wizard.ServiceClassPolicy;
import org.glite.authz.pap.ui.wizard.ServiceClassPolicySet;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xml.ConfigurationException;

public class Test1 {

    public static void main(String[] args) throws EncodingException, ConfigurationException {
        
        PAPConfiguration.bootstrap();
        pln("Helloworld!");
        
        PolicySetWizard policySet = new LocalPAPPolicySet();
        BlacklistPolicySet blPS = new BlacklistPolicySet();
        ServiceClassPolicySet scPS = new ServiceClassPolicySet();
        policySet.addPolicySet(blPS);
        policySet.addPolicySet(scPS);
        
        List<AttributeWizard> attributeList = new ArrayList<AttributeWizard>(1);
        attributeList.add(new AttributeWizard("fqan", "fqan_VAL"));
        
        BlacklistPolicy blP = new BlacklistPolicy(attributeList, null);
        ServiceClassPolicy scP = new ServiceClassPolicy(attributeList, null, EffectType.Permit);
        
        blPS.addPolicy(blP);
        scPS.addPolicy(scP);
        
        pln("SIZE=" + policySet.getPolicyTreeAsList().size());
        
        for (XACMLObject elem:policySet.getPolicyTreeAsList()) {
            if (elem instanceof PolicySetType) {
                PolicySetType ps = (PolicySetType) elem;
                pln("Writing PolicySet: " + ps.getPolicySetId());
                PolicySetHelper.getInstance().toFile("/tmp/PolicySet_" + ps.getPolicySetId(), ps);
            } else if (elem instanceof PolicyType) {
                PolicyType p = (PolicyType) elem;
                pln("Writing Policy   : " + p.getPolicyId());
                PolicyHelper.getInstance().toFile("/tmp/Policy_" + p.getPolicyId(), p);
            } else {
                pln("Error: not a PolicySetType or PolicyType");
            }
        }
        
        
//        PolicyFileEncoder pse = new PolicyFileEncoder();
//        
//        List<XACMLObject> list = pse.parse(new File("/var/remote_pap_list.txt"));
//        
//        for (XACMLObject xacmlObject : list) {
//            if (xacmlObject instanceof PolicySetType) {
//                System.out.println("PolicySet: " + ((PolicySetType) xacmlObject).getPolicySetId());
//                PolicySetHelper.getInstance().toFile("/tmp/PolicySet_" + ((PolicySetType)xacmlObject).getPolicySetId() + ".xml", (PolicySetType)xacmlObject);
//            } else if (xacmlObject instanceof PolicyType) {
//                System.out.println("Policy   : " + ((PolicyType) xacmlObject).getPolicyId());
//                // PolicyHelper.getInstance().toFile("/tmp/Policy_" +
//                // ((PolicyType)xacmlObject).getPolicyId() + ".xml",
//                // (PolicyType)xacmlObject);
//            }
//        }
        
        pln("Game over");
        
    }
    
    private static void pln(String s) {
        System.out.println(s);
    }

}
