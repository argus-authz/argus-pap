package org.glite.authz.pap.distribution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.distribution.exceptions.DistributionConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionConfiguration {

    private static final String REMOTE_PAPS_STANZA = "remote-paps";
    private static final String CONFIGURATION_STANZA = "distribution-configuration";

    private static final Logger log = LoggerFactory.getLogger(DistributionConfiguration.class);
    private static DistributionConfiguration instance = null;

    public static DistributionConfiguration getInstance() {
        if (instance == null)
            instance = new DistributionConfiguration();
        return instance;
    }

    private static String aliasKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias;
    }
    
    private static String dnKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "dn";
    }

    private static String endpointKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "endpoint";
    }
    
    private static String hostnameKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "hostname";
    }
    
    private static String pathKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "path";
    }
    
    private static String portKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "port";
    }
    
    private static String publicVisibilityKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "public_visibility";
    }
    
    private static String protocolKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "protocol";
    }

    private static String minKeyLengthKey() {
        return CONFIGURATION_STANZA + "." + "min-key-length";
    }

    private static String minKeyLengthKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "min-key-length";
    }

    private static String papOrderKey() {
        return CONFIGURATION_STANZA + "." + "pap-order";
    }

    private static String pollIntervallKey() {
        return CONFIGURATION_STANZA + "." + "poll-interval";
    }

    private PAPConfiguration papConfiguration;

    public DistributionConfiguration() {
        papConfiguration = PAPConfiguration.instance();
    }
    
    public long getPollIntervallInMillis() {

        long pollIntervalInSecs = papConfiguration.getLong(pollIntervallKey());
        log.info("Polling interval for remote PAPs is set to: " + pollIntervalInSecs + " seconds");

        return pollIntervalInSecs * 1000;

    }

    @SuppressWarnings("unchecked")
    public List<PAP> getRemotePAPList() {

        List<PAP> papList = new LinkedList<PAP>();

        Set<String> papAliasSet = new HashSet<String>();
        Iterator iterator = papConfiguration.getKeys(REMOTE_PAPS_STANZA);

        // Get the set of PAP aliases
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            int firstAliasChar = key.indexOf('.') + 1;
            String papAlias = key.substring(firstAliasChar, key.indexOf('.', firstAliasChar));
            papAliasSet.add(papAlias);
        }

        // Get an ordered list of PAP aliases
        List<String> papAliasList = new ArrayList<String>(papAliasSet.size());
        String[] papOrder = getPAPOrderArray();

        if (papOrder != null) {
            for (String papAlias : papOrder) {
                if (papAliasSet.remove(papAlias)) {
                    papAliasList.add(papAlias);
                } else {
                    throw new DistributionConfigurationException("Undefined PAP alias \"" + papAlias
                            + "\" in \"pap-order\"");
                }
            }
        }

        // Order can be partially defined so... get the remaining aliases
        for (String papAlias : papAliasSet) {
            papAliasList.add(papAlias);
        }

        // Build the list of PAP objects
        for (String papAlias : papAliasList) {

            String dn = papConfiguration.getString(dnKey(papAlias));
            if (dn == null) {
                throw new DistributionConfigurationException("DN is not set for remote PAP \""
                        + papAlias + "\"");
            }

            String hostname = papConfiguration.getString(hostnameKey(papAlias));
            if (hostname == null)
                throw new DistributionConfigurationException("Hostname is not set for remote PAP \""
                        + papAlias + "\"");
            
            // port can be null or empty
            String port = papConfiguration.getString(portKey(papAlias));
            // path can be null or empty
            String path = papConfiguration.getString(pathKey(papAlias));
            // protocol can be null or empty
            String protocol = papConfiguration.getString(protocolKey(papAlias));
            boolean visibilityPublic = papConfiguration.getBoolean(publicVisibilityKey(papAlias));
            
            PAP pap = new PAP(papAlias, dn, hostname, port, path, protocol, visibilityPublic);

            log.info("Adding remote PAP: " + pap);
            
            papList.add(pap);
        }
        
        if (papAliasList.isEmpty())
            log.info("No remote PAPs has been defined");

        return papList;
    }
    
    public void removePAP(String papAlias) {
    	
        clearPAPProperties(papAlias);
        
        // TODO: remove PAP from pap-order
//        String[] papOrderArrayOld = getPAPOrderArray();
//        List<String> papOrderList = new ArrayList<String>(papOrderArrayOld.length);
//        for (String pap:papOrderArrayOld) {
//            if (!(pap.equals(papAlias)))
//                papOrderList.add(pap);
//        }
//        String[] papOrderArrayNew = (String[]) papOrderList.toArray();
//        
//        setPAPOrder(papOrderArrayNew);
        
        papConfiguration.saveStartupConfiguration();
    }
    
    public void setPAPAndSave(PAP pap) {
        setPAPProperties(pap);
        papConfiguration.saveStartupConfiguration();
    }
    
    public String[] getPAPOrderArray() {
        return papConfiguration.getStringArray(papOrderKey());
    }
    
    public void setPAPOrder(String[] papArray) {
        papConfiguration.clearDistributionProperty(papOrderKey());

        if (papArray == null)
            return;
        
        if (papArray.length == 0)
            return;
        
        StringBuilder sb = new StringBuilder(papArray[0]);
        
        for (int i=0; i<papArray.length; i++) {
            sb.append(", " + papArray[i]);
        }
        
        papConfiguration.setDistributionProperty(papOrderKey(), sb.toString());
        
    }
    
    private void setPAPProperties(PAP pap) {
    	String papAlias = pap.getAlias();
        
        papConfiguration.setDistributionProperty(dnKey(papAlias), pap.getDn());
        
        papConfiguration.setDistributionProperty(hostnameKey(papAlias), pap.getHostname());
        papConfiguration.setDistributionProperty(portKey(papAlias), pap.getPort());
        papConfiguration.setDistributionProperty(pathKey(papAlias), pap.getPath());
        papConfiguration.setDistributionProperty(protocolKey(papAlias), pap.getProtocol());
        
        String visibilityPublic;
        if (pap.isVisibilityPublic())
            visibilityPublic = "true";
        else
            visibilityPublic = "false";
        papConfiguration.setDistributionProperty(publicVisibilityKey(papAlias), visibilityPublic);
    }
    
    private void clearPAPProperties(String papAlias) {
        papConfiguration.clearDistributionProperty(dnKey(papAlias));
        papConfiguration.clearDistributionProperty(hostnameKey(papAlias));
        papConfiguration.clearDistributionProperty(portKey(papAlias));
        papConfiguration.clearDistributionProperty(pathKey(papAlias));
        papConfiguration.clearDistributionProperty(protocolKey(papAlias));
        papConfiguration.clearDistributionProperty(publicVisibilityKey(papAlias));
    }
    
}
