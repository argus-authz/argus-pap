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
    private static final String CONFIGURATION_STANZA = "configuration";

    private static final Logger log = LoggerFactory.getLogger(DistributionConfiguration.class);
    private static final DistributionConfiguration instance = new DistributionConfiguration();

    public static DistributionConfiguration getInstance() {
        return instance;
    }

    private static String dnKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "dn";
    }

    private static String endpointKey(String papAlias) {
        return REMOTE_PAPS_STANZA + "." + papAlias + "." + "endpoint";
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

    private final PAPConfiguration papConfiguration = PAPConfiguration.instance();

    private DistributionConfiguration() {}

    public long getPollIntervallInMillis() {

        long pollIntervalInSecs = papConfiguration.getLong(pollIntervallKey());
        log.info("Polling interval for remote PAPs is set to: " + pollIntervalInSecs);

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
        String[] papOrder = papConfiguration.getStringArray(papOrderKey());

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

            String endpoint = papConfiguration.getString(endpointKey(papAlias));
            if (endpoint == null) {
                throw new DistributionConfigurationException("Endpoint is not set for remote PAP \""
                        + papAlias + "\"");
            }

            PAP pap = new PAP(endpoint, dn);

            log.info("Adding remote PAP: " + pap);
            papList.add(pap);
        }

        return papList;
    }
}
