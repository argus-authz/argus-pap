package org.glite.authz.pap.common.xacml.wizard;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;

public class LocalPolicySetWizard {
	
	private Map<String, List<PolicyWizard>> resourceGroupMap = new HashMap<String, List<PolicyWizard>>(); 
	private Map<String, Map<String, List<PolicyWizard>>> serviceClassGroupMap = new HashMap<String, Map<String,List<PolicyWizard>>>();
	
	public void addPolicies(List<PolicyWizard> policyList) {
		for (PolicyWizard policy:policyList) {
			addPolicy(policy);
		}
	}
	
	public void addPolicy(PolicyWizard policy) {
		 
		 if (policy.isBlacklistPolicy())
			 addBlackListPolicy(policy);
		 else
			 addServiceClassPolicy(policy);
		
	}
	
	public void printFormattedBlacklistPolicies(PrintStream printStream) {
	    printFormattedBlacklistPolicies(printStream, false);
	}
	
	public void printFormattedBlacklistPolicies(PrintStream printStream, boolean noId) {
        printFormattedResourceGroup(printStream, 0, resourceGroupMap, noId);
    }
	
	public void printFormattedServiceClassPolicies(PrintStream printStream) {
	    printFormattedServiceClassPolicies(printStream, false);
	}
	
	public void printFormattedServiceClassPolicies(PrintStream printStream, boolean noId) {
	    Set<String> keySet = serviceClassGroupMap.keySet();
        
        for (String serviceClassValue:keySet) {
            
            printStream.println("service_class \"" + serviceClassValue + "\" {");
            
            printFormattedResourceGroup(printStream, 4, serviceClassGroupMap.get(serviceClassValue), noId);
            
            printStream.println("}");
            
        }
	}
	
	private void addBlackListPolicy(PolicyWizard policy) {
		
		List<PolicyWizard> policyList;
		String resourceValue = getAttributeValue(policy.getTargetAttributeWizardList(), AttributeWizardType.RESOURCE_URI);
		 
		 if (resourceGroupMap.containsKey(resourceValue)) {
			 policyList = resourceGroupMap.get(resourceValue);
		 } else {
			 policyList =  new LinkedList<PolicyWizard>();
			 resourceGroupMap.put(resourceValue, policyList);
		 }
		 
		 policyList.add(policy);
		
	}
	
	private void addServiceClassPolicy(PolicyWizard policy) {

		List<PolicyWizard> policyList;
		Map<String, List<PolicyWizard>> resourceMap;
		String resourceValue = getAttributeValue(policy.getTargetAttributeWizardList(), AttributeWizardType.RESOURCE_URI);
		String serviceClassValue = getAttributeValue(policy.getTargetAttributeWizardList(), AttributeWizardType.SERVICE_CLASS);
		
		if (serviceClassGroupMap.containsKey(serviceClassValue)) {
			resourceMap = serviceClassGroupMap.get(serviceClassValue);
			if (resourceMap.containsKey(resourceValue)) {
				policyList = resourceMap.get(resourceValue);
			} else {
				policyList = new LinkedList<PolicyWizard>();
				resourceMap.put(resourceValue, policyList);
			}
		} else {
			resourceMap = new HashMap<String, List<PolicyWizard>>();
			policyList = new LinkedList<PolicyWizard>();
			resourceMap.put(resourceValue, policyList);
			serviceClassGroupMap.put(serviceClassValue, resourceMap);
		}
		
		policyList.add(policy);
		
	}
	
	private String fillwithSpaces(int n) {
    	String s = "";
    	
    	for (int i=0; i<n; i++)
    		s += " ";
    	
    	return s;
    }
	
	private String getAttributeValue(List<AttributeWizard> targetAttributeList, AttributeWizardType attributeType) {
		
		for (AttributeWizard attribute:targetAttributeList) {
			if (attributeType.equals(attribute.getAttributeWizardType()))
				return attribute.getValue();
		}
		
		return "BUG";
	}
	
	private void printFormattedResourceGroup(PrintStream printStream, int indent, Map<String, List<PolicyWizard>> resourceGroupMap, boolean noId) {

		String indentString = fillwithSpaces(indent);
		Set<String> keySet = resourceGroupMap.keySet();
		
		for (String resourceValue:keySet) {
			printStream.println(indentString + "resource_uri \"" + resourceValue + "\" {");
			
			List<PolicyWizard> policyWizardList = resourceGroupMap.get(resourceValue);
			for (PolicyWizard policyWizard:policyWizardList) {
				printStream.println(policyWizard.toNormalizedFormattedString(indent + 4, noId));
			}
			
			printStream.println(indentString + "}");
		}
	}

}
