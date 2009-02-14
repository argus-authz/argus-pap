package org.glite.authz.pap.distribution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.BlacklistPolicySet;
import org.glite.authz.pap.common.xacml.wizard.ServiceClassPolicySet;
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

    protected DistributionConfiguration distributionConfiguration;
    protected PAPDAO papDAO;
    protected List<PAP> trustedRemotePAPsList;

    private PAPManager() {

        papDAO = RepositoryManager.getDAOFactory().getPAPDAO();

        createLocalPAPIfNotExists();

        distributionConfiguration = DistributionConfiguration.getInstance();

        initPAPList();
    }

    public static PAPManager getInstance() {

        if (instance == null) {
            throw new PAPConfigurationException("Please initialize configuration before calling the instance method!");
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
        trustedRemotePAPsList.add(pap);
        papDAO.store(pap);

        return new PAPContainer(pap);
    }

    public void createLocalPAPIfNotExists() {

        if (localPAPExists())
            return;

        PAP localPAP = new PAP(PAP.LOCAL_PAP_ALIAS, PAP.LOCAL_PAP_ALIAS, "localhost", true);
        localPAP.setPapId(PAP.LOCAL_PAP_ID);

        papDAO.store(localPAP);

        PAPContainer localPAPContainer = getLocalPAPContainer();

        PolicySetType localPolicySet = PolicySetHelper.buildWithAnyTarget(localPAP.getPapId(),
                PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);

        localPAPContainer.storePolicySet((new BlacklistPolicySet()).getPolicySetType());
        localPAPContainer.storePolicySet((new ServiceClassPolicySet()).getPolicySetType());

        PolicySetHelper.addPolicySetReference(localPolicySet, BlacklistPolicySet.POLICY_SET_ID);
        PolicySetHelper.addPolicySetReference(localPolicySet, ServiceClassPolicySet.POLICY_SET_ID);

        localPAPContainer.storePolicySet(localPolicySet);
    }

    public PAP deleteTrustedPAP(String papAlias) throws NotFoundException {
        PAP pap = getPAP(papAlias);
        distributionConfiguration.removePAP(papAlias);
        trustedRemotePAPsList.remove(pap);
        papDAO.delete(papAlias);
        return pap;
    }

    public boolean exists(String papAlias) {
        for (PAP pap : trustedRemotePAPsList) {
            if (pap.getAlias().equals(papAlias))
                return true;
        }
        return false;
    }
    
    public List<PAPContainer> getAllPAPContainer() {
        
        int papListSize = trustedRemotePAPsList.size() + 1;
        List<PAPContainer> papList = new ArrayList<PAPContainer>(papListSize);
        
        String[] papOrderArray = distributionConfiguration.getPAPOrderArray();
        
        int localPAPIdx = 0;
        
        for (int i=0; i<papOrderArray.length; i++) {
            
            String papAlias = papOrderArray[i];
            
            if (PAP.LOCAL_PAP_ALIAS.equals(papAlias)) {
                localPAPIdx = i;
                break;
            }
        }
        
        for (int i=0, j=0; i<papListSize; i++) {
            
            if (i == localPAPIdx) {
                papList.add(getLocalPAPContainer());
                log.info("ADDING Local");
            } else {
                String papAlias = trustedRemotePAPsList.get(j).getAlias();
                j++;
                papList.add(getTrustedPAPContainer(papAlias));
                
                log.info("ADDING " + papAlias);
            }
        }
        
        log.info("RETURNING NUMERO: " + papList.size());
        
        return papList;
    }

    public List<PAP> getAllTrustedPAPs() {
        return new ArrayList<PAP>(trustedRemotePAPsList);
    }

    public List<PAPContainer> getAllTrustedPAPsContainer() {
        List<PAPContainer> papContainerList = new ArrayList<PAPContainer>(trustedRemotePAPsList.size());
        for (PAP pap : trustedRemotePAPsList) {
            papContainerList.add(new PAPContainer(pap));
        }
        return papContainerList;
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
        for (PAP pap : trustedRemotePAPsList) {
            if (pap.getAlias().equals(papAlias))
                return pap;
        }

        log.debug("Requested PAP not found:" + papAlias);
        throw new NotFoundException("PAP not found: " + papAlias);
    }

    public List<PAP> getPublicTrustedPAPs() {

        List<PAP> resultList = new LinkedList<PAP>();

        for (PAP pap : trustedRemotePAPsList) {
            if (pap.isVisibilityPublic())
                resultList.add(pap);
        }

        return resultList;
    }

    public List<PAPContainer> getPublicTrustedPAPsContainer() {
        List<PAPContainer> papContainerList = new LinkedList<PAPContainer>();
        for (PAP pap : trustedRemotePAPsList) {
            if (pap.isVisibilityPublic())
                papContainerList.add(new PAPContainer(pap));
        }
        return papContainerList;
    }

    public PAPContainer getTrustedPAPContainer(String papAlias) {
        return new PAPContainer(getPAP(papAlias));
    }

    public String[] getTrustedPAPOrder() {
        return distributionConfiguration.getPAPOrderArray();
    }

    public void setTrustedPAPOrder(String[] aliasArray) {
        distributionConfiguration.savePAPOrder(aliasArray);
        // updated the internal list with the new order
        trustedRemotePAPsList = distributionConfiguration.getRemotePAPList();
    }

    public void updateTrustedPAP(String papAlias, PAP newpap) {

        if (PAP.LOCAL_PAP_ALIAS.equals(papAlias)) {
            updateLocalPAP(newpap);
            return;
        }

        boolean found = false;

        for (int i = 0; i < trustedRemotePAPsList.size(); i++) {
            PAP pap = trustedRemotePAPsList.get(i);
            if (pap.getAlias().equals(papAlias)) {
                trustedRemotePAPsList.set(i, newpap);
                found = true;
                break;
            }
        }

        if (!found)
            throw new NotFoundException("PAP not found (id=" + papAlias + ")");
    }

    private void initPAPList() {

        // synchronize the PAPs stored in the repository with the
        // ones defined in the distribution configuration

        trustedRemotePAPsList = new LinkedList<PAP>();
        List<PAP> papListFromConfiguration = DistributionConfiguration.getInstance().getRemotePAPList();

        if (papListFromConfiguration.isEmpty())
            log.info("No remote PAPs has been defined");

        // follow the order of the distribution config PAP list
        for (PAP papFromConfiguration : papListFromConfiguration) {
            try {

                String papAlias = papFromConfiguration.getAlias();

                PAP papFromRepository = papDAO.get(papAlias);
                
                if (papFromConfiguration.equals(papFromRepository)) {
                    trustedRemotePAPsList.add(papFromRepository);
                    continue;
                } else {
                    // PAP exists in the repository but must be updated
                    papDAO.update(papFromConfiguration);
                    log.info("Settings for PAP \"" + papAlias + "\" has been updated. Invalidating cache");
                }
            } catch (NotFoundException e) {
                // the PAP is not in the repository therefore store it.
                papDAO.store(papFromConfiguration);
            }
        }

        // remove from the repository PAPs that are not in the distribution configuration
        for (String alias : papDAO.getAllAliases()) {

            if (alias.equals(PAP.LOCAL_PAP_ALIAS))
                continue;

            if (exists(alias))
                continue;

            papDAO.delete(alias);
            
            log.info("Removed PAP \"" + alias + "\" from the repository because it hasn't been found in the configuration file.");
        }
    }

    private boolean localPAPExists() {
        return papDAO.exists(PAP.LOCAL_PAP_ALIAS);
    }

    private void updateLocalPAP(PAP newLocalPAP) {

        if (!PAP.LOCAL_PAP_ALIAS.equals(newLocalPAP.getAlias())) {
            throw new RepositoryException("Invalid alias for local PAP. Cannot perform updateLocalPAP request.");
        }

        if (!PAP.LOCAL_PAP_ID.equals(newLocalPAP.getPapId())) {
            throw new RepositoryException("Invalid id for local PAP. Cannot perform updateLocalPAP request.");
        }

        papDAO.update(newLocalPAP);

    }

}
