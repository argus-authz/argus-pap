package org.glite.authz.pap.papmanagement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.PAPConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionConfiguration {

    private static DistributionConfiguration instance = null;
    private static final Logger log = LoggerFactory.getLogger(DistributionConfiguration.class);

    private static final String PAPS_STANZA = "paps";
    private static final String PAPS_PROPERTIES_STANZA = "paps:properties";
    
    private PAPConfiguration papConfiguration;

    private DistributionConfiguration() {
        papConfiguration = PAPConfiguration.instance();
    }

    public static DistributionConfiguration getInstance() {
        if (instance == null)
            instance = new DistributionConfiguration();
        return instance;
    }

    private static String aliasKey(String papAlias) {
        return PAPS_STANZA + "." + papAlias;
    }

    private static String dnKey(String papAlias) {
        return aliasKey(papAlias) + "." + "dn";
    }

    private static String hostnameKey(String papAlias) {
        return aliasKey(papAlias) + "." + "hostname";
    }

    @SuppressWarnings("unused")
    private static String minKeyLengthKey() {
        return PAPS_STANZA + "." + "min_key_length";
    }

    @SuppressWarnings("unused")
    private static String minKeyLengthKey(String papAlias) {
        return aliasKey(papAlias) + "." + "min_key_length";
    }

    private static String papOrderKey() {
        return PAPS_PROPERTIES_STANZA + "." + "ordering";
    }

    private static String pathKey(String papAlias) {
        return aliasKey(papAlias) + "." + "path";
    }

    private static String pollIntervallKey() {
        return PAPS_PROPERTIES_STANZA + "." + "poll_interval";
    }

    private static String portKey(String papAlias) {
        return aliasKey(papAlias) + "." + "port";
    }

    private static String protocolKey(String papAlias) {
        return aliasKey(papAlias) + "." + "protocol";
    }

    private static String typeKey(String papAlias) {
        return aliasKey(papAlias) + "." + "type";
    }

    private static String visibilityPublicKey(String papAlias) {
        return aliasKey(papAlias) + "." + "public";
    }

    public String[] getPAPOrderArray() throws AliasNotFoundException {

        String[] papOrderArray = papConfiguration.getStringArray(papOrderKey());

        if (papOrderArray == null) {
            papOrderArray = new String[0];
        }

        validatePAPOrder(papOrderArray);

        return papOrderArray;
    }

    public long getPollIntervallInMilliSecs() {

        long pollIntervalInSecs = papConfiguration.getLong(pollIntervallKey());

        log.info("Polling interval for remote PAPs is set to: " + pollIntervalInSecs + " seconds");

        return pollIntervalInSecs * 1000;
    }

    public Pap[] getRemotePAPArray() {

        Set<String> aliasSet = getAliasSet();

        Pap[] papArray = new Pap[aliasSet.size()];

        int idx = 0;
        for (String papAlias : aliasSet) {

            if (Pap.DEFAULT_PAP_ALIAS.equals(papAlias)) {
                continue;
            }

            Pap pap = getPAPFromProperties(papAlias);
            papArray[idx] = pap;
            idx++;

            log.info("Adding remote PAP \"" + pap + "\" from configuarion");
        }

        return papArray;
    }

    public void removePAP(String papAlias) {

        clearPAPProperties(papAlias);

        String[] oldAliasOrderArray = getPAPOrderArray();

        int newArraySize = oldAliasOrderArray.length - 1;

        if (newArraySize >= 0) {
            String[] newAliasOrderArray = new String[newArraySize];

            for (int i = 0, j = 0; i < oldAliasOrderArray.length; i++) {

                String aliasItem = oldAliasOrderArray[i];

                if (!(aliasItem.equals(papAlias))) {
                    if (j < newArraySize) {
                        newAliasOrderArray[j] = aliasItem;
                        j++;
                    }
                }
            }
            savePAPOrder(newAliasOrderArray);
        }

        papConfiguration.saveStartupConfiguration();
    }

    public void savePAP(Pap pap) {
        setPAPProperties(pap);
        papConfiguration.saveStartupConfiguration();
    }

    public void savePAPOrder(String[] aliasArray) throws AliasNotFoundException {

        if (aliasArray == null) {
            papConfiguration.clearDistributionProperty(papOrderKey());
            papConfiguration.saveStartupConfiguration();
            return;
        }

        validatePAPOrder(aliasArray);

        if (aliasArray.length == 0) {
            papConfiguration.clearDistributionProperty(papOrderKey());
            papConfiguration.saveStartupConfiguration();
            return;
        }

        StringBuilder sb = new StringBuilder(aliasArray[0]);

        for (int i = 1; i < aliasArray.length; i++) {

            sb.append(", " + aliasArray[i]);
        }

        log.info("Setting new PAP order to: " + sb.toString());

        papConfiguration.clearDistributionProperty(papOrderKey());
        papConfiguration.setDistributionProperty(papOrderKey(), aliasArray);
        papConfiguration.saveStartupConfiguration();
    }

    private boolean aliasExists(String alias) {

        if (isEmpty(alias))
            return false;

        if (Pap.DEFAULT_PAP_ALIAS.equals(alias)) {
            return true;
        }

        String value = papConfiguration.getString(typeKey(alias));

        if (isEmpty(value))
            return false;

        return true;
    }

    private void clearPAPProperties(String papAlias) {
        papConfiguration.clearDistributionProperty(dnKey(papAlias));
        papConfiguration.clearDistributionProperty(hostnameKey(papAlias));
        papConfiguration.clearDistributionProperty(portKey(papAlias));
        papConfiguration.clearDistributionProperty(pathKey(papAlias));
        papConfiguration.clearDistributionProperty(protocolKey(papAlias));
        papConfiguration.clearDistributionProperty(visibilityPublicKey(papAlias));
        papConfiguration.clearDistributionProperty(typeKey(papAlias));
    }

    @SuppressWarnings("unchecked")
    private Set<String> getAliasSet() {

        Set<String> aliasSet = new HashSet<String>();

        Iterator iterator = papConfiguration.getKeys(PAPS_STANZA);
        while (iterator.hasNext()) {

            String key = (String) iterator.next();

            int firstAliasChar = key.indexOf('.') + 1;
            int lastAliasChar = key.indexOf('.', firstAliasChar);

            String alias = key.substring(firstAliasChar, lastAliasChar);

            aliasSet.add(alias);
        }

        return aliasSet;
    }

    private Pap getPAPFromProperties(String papAlias) {

        String type = papConfiguration.getString(typeKey(papAlias));
        if (type == null) {
            throw new DistributionConfigurationException("\"type\" is not set for remote PAP \"" + papAlias + "\"");
        }
        
        boolean isLocal = Pap.isLocal(type);
        
        String dn = papConfiguration.getString(dnKey(papAlias));
        String hostname = papConfiguration.getString(hostnameKey(papAlias));
        
        if ((hostname == null) && (!isLocal)) {
            throw new DistributionConfigurationException("\"hostname\" is not set for remote PAP \"" + papAlias + "\"");
        }
        
        String port = papConfiguration.getString(portKey(papAlias));
        String path = papConfiguration.getString(pathKey(papAlias));
        String protocol = papConfiguration.getString(protocolKey(papAlias));
        boolean visibilityPublic = papConfiguration.getBoolean(visibilityPublicKey(papAlias));

        return new Pap(papAlias, isLocal, dn, hostname, port, path, protocol, visibilityPublic);
    }

    private boolean isEmpty(String s) {
        if (s == null)
            return true;
        if (s.length() == 0)
            return true;
        return false;
    }

    private void setPAPProperties(Pap pap) {

        String papAlias = pap.getAlias();

        papConfiguration.setDistributionProperty(typeKey(papAlias), pap.getTypeAsString());
        papConfiguration.setDistributionProperty(dnKey(papAlias), pap.getDn());
        papConfiguration.setDistributionProperty(hostnameKey(papAlias), pap.getHostname());
        papConfiguration.setDistributionProperty(portKey(papAlias), pap.getPort());
        papConfiguration.setDistributionProperty(pathKey(papAlias), pap.getPath());
        papConfiguration.setDistributionProperty(protocolKey(papAlias), pap.getProtocol());
        papConfiguration.setDistributionProperty(visibilityPublicKey(papAlias), pap.isVisibilityPublic());
    }

    private void validatePAPOrder(String[] aliasArray) {

        if (aliasArray == null) {
            throw new DistributionConfigurationException("aliasArray is null");
        }

        Set<String> aliasSet = new HashSet<String>(aliasArray.length);

        for (String alias : aliasArray) {

            if (aliasSet.contains(alias)) {
                throw new DistributionConfigurationException(
                    String.format("Error in remote PAPs order: alias \"%s\" appears more than one time", alias));
            }

            aliasSet.add(alias);

            if (!aliasExists(alias)) {
                throw new AliasNotFoundException(String.format("Error in remote PAPs order: unknown alias \"%s\"", alias));
            }
        }
    }

}
