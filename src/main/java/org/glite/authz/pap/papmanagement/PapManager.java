package org.glite.authz.pap.papmanagement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.Pap;
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

        createDefaultPapIfNotExists();

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

    public void addPap(Pap pap) {

        String papAlias = pap.getAlias();

        if (Pap.DEFAULT_PAP_ALIAS.equals(papAlias)) {
            throw new PapManagerException("Forbidden alias: " + papAlias);
        }

        if (exists(papAlias)) {
            throw new AlreadyExistsException("PAP \"" + papAlias + "\" already exists");
        }

        distributionConfiguration.savePAP(pap);
        papDAO.store(pap);

        // create the root policy set
        PolicySetType rootPolicySet = PolicySetHelper.buildWithAnyTarget(pap.getId(),
                                                                         PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);
        rootPolicySet.setVersion("0");

        PapContainer papContainer = new PapContainer(pap);
        papContainer.storePolicySet(rootPolicySet);
    }

    public void deletePap(String papAlias) throws NotFoundException {

        if (Pap.DEFAULT_PAP_ALIAS.equals(papAlias)) {
            throw new PapManagerException("Delete the default PAP is not allowed");
        }

        distributionConfiguration.removePAP(papAlias);
        papDAO.delete(papAlias);
    }

    public boolean exists(String papAlias) {
        return papDAO.exists(papAlias);
    }

    public List<Pap> getAllPaps() {
        return getPapList();
    }

    public List<Pap> getLocalPaps() {

        List<Pap> resultList = new LinkedList<Pap>();

        for (Pap pap : getPapList()) {
            if (pap.isLocal()) {
                resultList.add(pap);
            }
        }
        return resultList;
    }

    public List<Pap> getOrderedRemotePaps() {

        List<Pap> remotePapList = new LinkedList<Pap>();

        for (Pap pap : getPapList()) {
            if (pap.isRemote()) {
                remotePapList.add(pap);
            }
        }
        return remotePapList;
    }

    public Pap getPap(String papAlias) throws NotFoundException {
        return papDAO.get(papAlias);
    }

    public PapContainer getPapContainer(String papAlias) {
        return new PapContainer(getPap(papAlias));
    }

    public String[] getPapOrdering() {
        return configurationAliasOrderedArray;
    }

    public List<Pap> getPublicPaps() {

        List<Pap> resultList = new LinkedList<Pap>();

        for (Pap pap : getPapList()) {
            if (pap.isVisibilityPublic()) {
                resultList.add(pap);
            }
        }
        return resultList;
    }
    
    public void setPapOrdering(String[] aliasArray) {
        distributionConfiguration.savePAPOrder(aliasArray);

        // updated the internal list with the new order
        configurationAliasOrderedArray = distributionConfiguration.getPAPOrderArray();
    }

    public void updatePap(Pap newpap) {
    	
    	String alias = newpap.getAlias();

        if (Pap.DEFAULT_PAP_ALIAS.equals(alias)) {
            updateDefaultPap(newpap);
            return;
        }
        
        Pap oldPAP = papDAO.get(alias);
        
        // id (and alias) cannot change
        newpap.setId(oldPAP.getId());

        papDAO.update(newpap);
    }

    private String[] buildOrderedAliasArray() {

        String[] repositoryAliasArray = papDAO.getAllAliases();

        int configurationArraySize = configurationAliasOrderedArray.length;

        if (configurationArraySize > repositoryAliasArray.length) {
            throw new PapManagerException("BUG: configuration contains more PAPs than the repository");
        }

        int defaultPAPAliasIdx = getAliasIndex(Pap.DEFAULT_PAP_ALIAS, repositoryAliasArray);

        if (defaultPAPAliasIdx == -1) {
            throw new PapManagerException("BUG: default PAP does not exist in the repository");
        }

        /* enforce the order specified in the configuration file */

        List<String> configurationAliasOrderedList = new LinkedList<String>();

        // if the default PAP is not specified in the order in configuration then it goes for first
        if (getAliasIndex(Pap.DEFAULT_PAP_ALIAS, configurationAliasOrderedArray) == -1) {
            configurationAliasOrderedList.add(Pap.DEFAULT_PAP_ALIAS);
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

    private void createDefaultPapIfNotExists() {

        Pap defaultPAP;

        if (!papDAO.exists(Pap.DEFAULT_PAP_ALIAS)) {

            defaultPAP = Pap.makeDefaultPAP();
            papDAO.store(defaultPAP);

        } else {
            defaultPAP = papDAO.get(Pap.DEFAULT_PAP_ALIAS);
        }

        // PAPContainer defaultPAPContainer = new PAPContainer(defaultPAP);
        PapContainer defaultPapContainer = getPapContainer(Pap.DEFAULT_PAP_ALIAS);

        // check if the root policy set exists
        if (defaultPapContainer.hasPolicySet(defaultPapContainer.getPAPRootPolicySetId())) {
            return;
        }

        // create the root policy set
        defaultPapContainer.createRootPolicySet();
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

    private List<Pap> getPapList() {
        String[] aliasOrderedArray = buildOrderedAliasArray();
        List<Pap> papList = new ArrayList<Pap>(aliasOrderedArray.length);

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

        Pap[] papListFromConfiguration = DistributionConfiguration.getInstance().getRemotePAPArray();

        if (papListFromConfiguration.length == 0) {
            log.info("No remote PAPs has been defined");
        }

        // make sure that all PAPs defined in the configuration are also in the
        // repository
        for (Pap papFromConfiguration : papListFromConfiguration) {
            try {

                String papAlias = papFromConfiguration.getAlias();

                Pap papFromRepository = papDAO.get(papAlias);

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
            if (alias.equals(Pap.DEFAULT_PAP_ALIAS)) {
                continue;
            }

            boolean notFoundInConfiguration = true;
            for (Pap papFromConfiguration : papListFromConfiguration) {
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

    private void updateDefaultPap(Pap newDefaultPAP) {

        if (!Pap.DEFAULT_PAP_ALIAS.equals(newDefaultPAP.getAlias())) {
            throw new RepositoryException("Invalid alias for default PAP. Cannot perform updateDefaultPAP request.");
        }

        Pap oldDefaultPAP = getPap(Pap.DEFAULT_PAP_ALIAS);

        newDefaultPAP.setId(oldDefaultPAP.getId());

        papDAO.update(newDefaultPAP);
    }

}
