package org.glite.authz.pap.papmanagement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.distribution.DistributionConfiguration;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PapDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PapManager {

    private static PapManager instance = null;
    private static final Logger log = LoggerFactory.getLogger(PapManager.class);

    private String[] configurationAliasOrderedArray;
    private DistributionConfiguration distributionConfiguration;
    private PapDAO papDAO;

    private PapManager() {

        papDAO = RepositoryManager.getDAOFactory().getPapDAO();

        createDefaultPAPIfNotExists();

        distributionConfiguration = DistributionConfiguration.getInstance();

        configurationAliasOrderedArray = distributionConfiguration.getPAPOrderArray();

        synchronizeRepositoryWithConfiguration();
    }

    public static PapManager getInstance() {

        if (instance == null) {
            throw new PapManagerException("Please initialize configuration before calling the instance method!");
        }

        return instance;
    }

    public static void initialize() {
        if (instance == null) {
            instance = new PapManager();
        }
    }

    public void addPAP(PAP pap) {

        String papAlias = pap.getAlias();

        if (PAP.DEFAULT_PAP_ALIAS.equals(papAlias)) {
            throw new PapManagerException("Forbidden alias: " + papAlias);
        }

        if (exists(papAlias)) {
            throw new AlreadyExistsException("PAP \"" + papAlias + "\" already exists");
        }

        distributionConfiguration.savePAP(pap);
        papDAO.store(pap);

        // create the root policy set
        PolicySetType rootPolicySet = PolicySetHelper.buildWithAnyTarget(pap.getPapId(),
                                                                         PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);
        rootPolicySet.setVersion("0");

        PapContainer papContainer = new PapContainer(pap);
        papContainer.storePolicySet(rootPolicySet);
    }

    public void createDefaultPAPIfNotExists() {

        PAP defaultPAP;

        if (!papDAO.exists(PAP.DEFAULT_PAP_ALIAS)) {

            defaultPAP = PAP.makeDefaultPAP();
            papDAO.store(defaultPAP);

        } else {
            defaultPAP = papDAO.get(PAP.DEFAULT_PAP_ALIAS);
        }

        // PAPContainer defaultPAPContainer = new PAPContainer(defaultPAP);
        PapContainer defaultPAPContainer = getDefaultPAPContainer();

        // check if the root policy set exists
        if (defaultPAPContainer.hasPolicySet(defaultPAPContainer.getPAPRootPolicySetId())) {
            return;
        }

        // create the root policy set
        defaultPAPContainer.createRootPolicySet();
    }

    public void deletePAP(String papAlias) throws NotFoundException {

        if (PAP.DEFAULT_PAP_ALIAS.equals(papAlias)) {
            throw new PapManagerException("Delete the default PAP is not allowed");
        }

        distributionConfiguration.removePAP(papAlias);
        papDAO.delete(papAlias);
    }

    public boolean exists(String papAlias) {
        return papDAO.exists(papAlias);
    }

    public List<PAP> getAllPAPs() {
        return getPAPList();
    }

    public PAP getDefaultPAP() {
        if (!papDAO.exists(PAP.DEFAULT_PAP_ALIAS)) {
            throw new NotFoundException("Critical error (probably a BUG): default PAP not found.");
        }
        return papDAO.get(PAP.DEFAULT_PAP_ALIAS);
    }

    public PapContainer getDefaultPAPContainer() {
        return new PapContainer(getDefaultPAP());
    }

    public List<PAP> getLocalPAPs() {

        List<PAP> resultList = new LinkedList<PAP>();

        for (PAP pap : getPAPList()) {
            if (pap.isLocal()) {
                resultList.add(pap);
            }
        }
        return resultList;
    }

    public List<PAP> getOrderedRemotePAPs() {

        List<PAP> remotePapList = new LinkedList<PAP>();

        for (PAP pap : getPAPList()) {
            if (pap.isRemote()) {
                remotePapList.add(pap);
            }
        }
        return remotePapList;
    }

    public PAP getPAP(String papAlias) throws NotFoundException {
        return papDAO.get(papAlias);
    }

    public String[] getPAPConfigurationOrder() {
        return configurationAliasOrderedArray;
    }

    public PapContainer getPAPContainer(String papAlias) {
        return new PapContainer(getPAP(papAlias));
    }
    
    public List<PAP> getPublicPAPs() {

        List<PAP> resultList = new LinkedList<PAP>();

        for (PAP pap : getPAPList()) {
            if (pap.isVisibilityPublic()) {
                resultList.add(pap);
            }
        }
        return resultList;
    }

    public void setPAPOrder(String[] aliasArray) {
        distributionConfiguration.savePAPOrder(aliasArray);

        // updated the internal list with the new order
        configurationAliasOrderedArray = distributionConfiguration.getPAPOrderArray();
    }

    public void updatePAP(PAP newpap) {
    	
    	String alias = newpap.getAlias();

        if (PAP.DEFAULT_PAP_ALIAS.equals(alias)) {
            updateDefaultPAP(newpap);
            return;
        }
        
        PAP oldPAP = papDAO.get(alias);
        
        // id (and alias) cannot change
        newpap.setPapId(oldPAP.getPapId());

        papDAO.update(newpap);
    }

    private String[] buildOrderedAliasArray() {

        String[] repositoryAliasArray = papDAO.getAllAliases();

        int configurationArraySize = configurationAliasOrderedArray.length;

        if (configurationArraySize > repositoryAliasArray.length) {
            throw new PapManagerException("BUG: configuration contains more PAPs than the repository");
        }

        int defaultPAPAliasIdx = getAliasIndex(PAP.DEFAULT_PAP_ALIAS, repositoryAliasArray);

        if (defaultPAPAliasIdx == -1) {
            throw new PapManagerException("BUG: default PAP does not exist in the repository");
        }

        /* enforce the order specified in the configuration file */

        List<String> configurationAliasOrderedList = new LinkedList<String>();

        // if the default PAP is not specified in the order in configuration then it goes for first
        if (getAliasIndex(PAP.DEFAULT_PAP_ALIAS, configurationAliasOrderedArray) == -1) {
            configurationAliasOrderedList.add(PAP.DEFAULT_PAP_ALIAS);
        }

        for (int i = 0; i < configurationArraySize; i++) {
            configurationAliasOrderedList.add(configurationAliasOrderedArray[i]);
        }

        // follow the order specified in the configuration file
        for (int i = 0; i < configurationAliasOrderedList.size(); i++) {
            String alias = configurationAliasOrderedList.get(i);

            int aliasIndex = getAliasIndex(alias, repositoryAliasArray);

            if (aliasIndex == -1) {
                throw new PapManagerException(
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

    private List<PAP> getPAPList() {
        String[] aliasOrderedArray = buildOrderedAliasArray();
        List<PAP> papList = new ArrayList<PAP>(aliasOrderedArray.length);

        for (String alias : aliasOrderedArray) {
            papList.add(papDAO.get(alias));
        }
        return papList;
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
            if (alias.equals(PAP.DEFAULT_PAP_ALIAS)) {
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

    private void updateDefaultPAP(PAP newDefaultPAP) {

        if (!PAP.DEFAULT_PAP_ALIAS.equals(newDefaultPAP.getAlias())) {
            throw new RepositoryException("Invalid alias for default PAP. Cannot perform updateDefaultPAP request.");
        }

        PAP oldDefaultPAP = getDefaultPAP();

        newDefaultPAP.setPapId(oldDefaultPAP.getPapId());

        papDAO.update(newDefaultPAP);
    }

}
