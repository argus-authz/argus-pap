package org.glite.authz.pap.repository.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.repository.dao.PapDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA implementation of the {@link PapDAO} interface.
 * <p>
 * This DAO stores information about the paps and the version of the repository. These information are written in an INI
 * file.
 */
public class PapDAOHibernate extends GenericDAOJpa implements PapDAO {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PapDAOHibernate.class);

    /**
     * Constructor.
     */
    private PapDAOHibernate() {
    }

    public static PapDAOHibernate getInstance() {
        return new PapDAOHibernate();
    }

    /**
     * {@inheritDoc}
     */
    public void delete(String papAlias) {

        EntityTransaction tx = manageTransaction();

        try {
            Pap pap = get(papAlias);

            PapContainer papContainer = new PapContainer(get(papAlias));
            papContainer.deleteAllPolicies();
            papContainer.deleteAllPolicySets();

            getEntityManager().remove(pap);

            commitManagedTransaction(tx);

        } catch (NotFoundException e) {
            rollbakManagedTransaction(tx);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(String papAlias) {

        try {
            get(papAlias);
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Pap get(String papAlias) {

        Pap pap = null;

        try {
            pap = (Pap) getEntityManager().createQuery("select p from Pap p where p.alias = :alias")
                                          .setParameter("alias", papAlias)
                                          .setMaxResults(1)
                                          .getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // do nothing. Use case for the following if, pap is null.
        }
        if (pap == null) {
            throw new NotFoundException(String.format("Not found: papAlias=%s", papAlias));
        }

        return pap;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getAliasList() {
        List<Pap> papList = getAll();

        List<String> aliasList = new ArrayList<String>(papList.size());

        for (Pap pap : papList) {
            aliasList.add(pap.getAlias());
        }
        return aliasList;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Pap> getAll() {

        List<Pap> papList = (List<Pap>) getEntityManager().createQuery("select p from Pap p").getResultList();
        return papList;
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        // TODO: implement this
        return "1";
    }

    /**
     * {@inheritDoc}
     */
    public void store(Pap pap) {

        EntityTransaction tx = manageTransaction();

        try {
            getEntityManager().persist(pap);
        } catch (ConstraintViolationException e) {
            rollbakManagedTransaction(tx);
            throw new AlreadyExistsException(String.format("Already exists: papAlias=%s", pap.getAlias()));
        }

        commitManagedTransaction(tx);
    }

    /**
     * {@inheritDoc}
     */
    public void update(Pap pap) {

        EntityTransaction tx = manageTransaction();

        try {
            Pap persistedPap = get(pap.getAlias());

            persistedPap.setAll(pap);

            getEntityManager().persist(persistedPap);

            commitManagedTransaction(tx);

        } catch (NotFoundException e) {
            rollbakManagedTransaction(tx);
        }
    }
}
