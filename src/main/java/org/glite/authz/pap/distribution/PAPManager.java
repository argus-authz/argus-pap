package org.glite.authz.pap.distribution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PAPDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPManager {

    private static PAPManager instance = null;
    private static final Logger log = LoggerFactory.getLogger(PAPManager.class);

    private DistributionConfiguration distributionConfiguration;
    private PAPDAO papDAO;
    private String[] configurationAliasOrderedArray;

    private PAPManager() {

        papDAO = RepositoryManager.getDAOFactory().getPAPDAO();

        createLocalPAPIfNotExists();

        distributionConfiguration = DistributionConfiguration.getInstance();

        configurationAliasOrderedArray = distributionConfiguration.getPAPOrderArray();

        synchronizeRepositoryWithConfiguration();
    }

    public static PAPManager getInstance() {

        if (instance == null) {
            throw new PAPManagerException("Please initialize configuration before calling the instance method!");
        }

        return instance;
    }

    public static void initialize() {
        if (instance == null) {
            instance = new PAPManager();
        }
    }

    public PAPContainer addTrustedPAP(PAP pap) {

        String papAlias = pap.getAlias();

        if (PAP.LOCAL_PAP_ALIAS.equals(papAlias)) {
            throw new AlreadyExistsException("Forbidden alias: " + papAlias);
        }

        if (exists(papAlias))
            throw new AlreadyExistsException("PAP \"" + papAlias + "\" already exists");

        distributionConfiguration.savePAP(pap);
        papDAO.store(pap);

        return new PAPContainer(pap);
    }

    public void createLocalPAPIfNotExists() {

        PAP localPAP;
        
        if (!papDAO.exists(PAP.LOCAL_PAP_ALIAS)) {
            
            localPAP = new PAP(PAP.LOCAL_PAP_ALIAS, PAP.LOCAL_PAP_ALIAS, "localhost", true);

            papDAO.store(localPAP);
        } else {
            localPAP = papDAO.get(PAP.LOCAL_PAP_ALIAS);
        }

        // check if the root policy set exists
        PAPContainer localPAPContainer = getLocalPAPContainer();
        
        if (localPAPContainer.hasPolicySet(localPAPContainer.getPAPRootPolicySetId())) {
            return;
        }

        PolicySetType localPolicySet = PolicySetHelper.buildWithAnyTarget(localPAP.getPapId(),
                PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);

        localPAPContainer.storePolicySet(localPolicySet);
    }

    public PAP deleteTrustedPAP(String papAlias) throws NotFoundException {
        PAP pap = getPAP(papAlias);
        distributionConfiguration.removePAP(papAlias);
        papDAO.delete(papAlias);
        return pap;
    }
    
    public boolean exists(String papAlias) {
        return papDAO.exists(papAlias);
    }

    public PAPContainer[] getOrderedPAPContainerArray() {

        String[] aliasOrderedArray = buildOrderedAliasArray();

        PAPContainer[] papContainerArray = new PAPContainer[aliasOrderedArray.length];

        for (int i = 0; i < aliasOrderedArray.length; i++) {
            papContainerArray[i] = new PAPContainer(getPAP(aliasOrderedArray[i]));
        }

        return papContainerArray;

    }

    public PAP[] getOrderedRemotePAPsArray() {

        String[] aliasOrderedArray = buildOrderedAliasArray();

        PAP[] papArray = new PAP[aliasOrderedArray.length - 1];

        for (int i = 0, j = 0; i < aliasOrderedArray.length; i++) {

            if (PAP.LOCAL_PAP_ALIAS.equals(aliasOrderedArray[i])) {
                continue;
            }
            papArray[j] = getPAP(aliasOrderedArray[i]);
            j++;
        }

        return papArray;
    }

    public PAPContainer[] getOrderedRemotePAPsContainerArray() {

        PAP[] papArray = getOrderedRemotePAPsArray();

        int size = papArray.length;

        PAPContainer[] papContainerArray = new PAPContainer[size];

        for (int i = 0; i < size; i++) {
            papContainerArray[i] = new PAPContainer(papArray[i]);
        }

        return papContainerArray;
    }

    public PAP getLocalPAP() {
        return papDAO.get(PAP.LOCAL_PAP_ALIAS);
    }

    public PAPContainer getLocalPAPContainer() {

        if (!localPAPExists()) {
            throw new NotFoundException("Critical error (probably a BUG): local PAP not found.");
        }

        return new PAPContainer(getLocalPAP());
    }

    public PAP getPAP(String papAlias) throws NotFoundException {
        return papDAO.get(papAlias);
    }

    public List<PAP> getPublicRemotePAPs() {

        String[] aliasOrderedArray = buildOrderedAliasArray();
        List<PAP> resultList = new LinkedList<PAP>();

        for (String alias : aliasOrderedArray) {

            if (alias.equals(PAP.LOCAL_PAP_ALIAS)) {
                continue;
            }

            PAP pap = papDAO.get(alias);

            if (pap.isVisibilityPublic()) {
                resultList.add(pap);
            }
        }
        return resultList;
    }

    public List<PAPContainer> getPublicRemotePAPsContainers() {

        List<PAP> papPublicPAPList = getPublicRemotePAPs();
        List<PAPContainer> papContainerList = new ArrayList<PAPContainer>(papPublicPAPList.size());

        for (PAP pap : papPublicPAPList) {
            papContainerList.add(new PAPContainer(pap));
        }
        return papContainerList;
    }

    public PAPContainer getRemotePAPContainer(String papAlias) {
        return new PAPContainer(getPAP(papAlias));
    }

    public String[] getRemotePAPConfigurationOrder() {
        return configurationAliasOrderedArray;
    }

    public void setPAPOrder(String[] aliasArray) {
        distributionConfiguration.savePAPOrder(aliasArray);
        // updated the internal list with the new order
        configurationAliasOrderedArray = distributionConfiguration.getPAPOrderArray();
    }

    public void updatePAP(String papAlias, PAP newpap) {

        if (PAP.LOCAL_PAP_ALIAS.equals(papAlias)) {
            updateLocalPAP(newpap);
            return;
        }

        papDAO.update(newpap);
    }

    private void synchronizeRepositoryWithConfiguration() {

        PAP[] papListFromConfiguration = DistributionConfiguration.getInstance().getRemotePAPArray();

        if (papListFromConfiguration.length == 0) {
            log.info("No remote PAPs has been defined");
        }

        // make sure that all PAPs defined in the configuration are also in the
        // repository
        for (PAP papFromConfiguration : papListFromConfiguration) {
            try {

                String papAlias = papFromConfiguration.getAlias();

                PAP papFromRepository = papDAO.get(papAlias);

                if (papFromConfiguration.equals(papFromRepository)) {
                    continue;
                }

                // PAP in the repository but must be updated with the
                // information
                // of the PAP found in configuration. The cache must be removed
                // so we can delete the PAP and store it again
                log.info("Settings for PAP \"" + papAlias + "\" has been updated. Invalidating cache");
                papDAO.delete(papAlias);

            } catch (NotFoundException e) {
                // the PAP is not in the repository therefore go on and store it
            }

            papDAO.store(papFromConfiguration);
        }

        // remove from the repository PAPs that are not in the distribution
        // configuration
        for (String alias : papDAO.getAllAliases()) {

            // do not remove the local PAP
            if (alias.equals(PAP.LOCAL_PAP_ALIAS)) {
                continue;
            }

            boolean notFoundInConfiguration = true;
            for (PAP papFromConfiguration : papListFromConfiguration) {
                if (alias.equals(papFromConfiguration.getAlias())) {
                    notFoundInConfiguration = false;
                    break;
                }
            }

            if (notFoundInConfiguration) {
                papDAO.delete(alias);
                log.info("Removed PAP \"" + alias
                        + "\" from the repository because it hasn't been found in the configuration file.");
            }
        }
    }

    private boolean localPAPExists() {
        return papDAO.exists(PAP.LOCAL_PAP_ALIAS);
    }

    private void updateLocalPAP(PAP newLocalPAP) {

        if (!PAP.LOCAL_PAP_ALIAS.equals(newLocalPAP.getAlias())) {
            throw new RepositoryException("Invalid alias for local PAP. Cannot perform updateLocalPAP request.");
        }
        
        PAP oldLocalPAP = getLocalPAP();
        
        newLocalPAP.setPapId(oldLocalPAP.getPapId());

        papDAO.update(newLocalPAP);
    }

    private String[] buildOrderedAliasArray() {

        String[] repositoryAliasArray = papDAO.getAllAliases();

        int configurationArraySize = configurationAliasOrderedArray.length;

        if (configurationArraySize > repositoryAliasArray.length) {
            throw new PAPManagerException("BUG: configuration contains more PAPs then repository");
        }

        int localPAPAliasIdx = getAliasIndex(PAP.LOCAL_PAP_ALIAS, repositoryAliasArray);

        if (localPAPAliasIdx == -1) {
            throw new PAPManagerException("BUG: local PAP does not exist in the repository");
        }

        if (getAliasIndex(PAP.LOCAL_PAP_ALIAS, configurationAliasOrderedArray) == -1) {
            // local PAP goes for first
            swapElementsOfArray(localPAPAliasIdx, 0, repositoryAliasArray);
        }

        for (int i = 0; i < configurationArraySize; i++) {
            String alias = configurationAliasOrderedArray[i];

            int aliasIndex = getAliasIndex(alias, repositoryAliasArray);

            if (aliasIndex == -1) {
                throw new PAPManagerException(
                        "BUG: initialization error. PAP defined in the configuration is not in the repository");
            }

            swapElementsOfArray(aliasIndex, i, repositoryAliasArray);
        }

        return repositoryAliasArray;
    }

    private int getAliasIndex(String alias, String[] aliasArray) {

        if (alias == null) {
            return -1;
        }

        for (int i = 0; i < aliasArray.length; i++) {

            if (alias.equals(aliasArray[i])) {
                return i;
            }
        }

        return -1;
    }

    private void swapElementsOfArray(int idx1, int idx2, String[] array) {

        if (idx1 == idx2) {
            return;
        }

        int size = array.length;

        if (size == 0) {
            return;
        }

        if ((idx1 < 0) || (idx1 >= size)) {
            return;
        }

        if ((idx2 < 0) || (idx2 >= size)) {
            return;
        }

        String temp = array[idx1];
        array[idx1] = array[idx2];
        array[idx2] = temp;

        return;
    }

}
