package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

/**
 * This is the interface for the Pap DAO.
 */
public interface PapDAO {

    /**
     * Delete a Pap.
     * <p>
     * The pap identified by the alias is deleted together with all the policies and policy sets it contains.
     * 
     * @param alias identifier of the pap.
     * @throws NotFoundException if the given <code>alias</code> was not found.
     */
    public void delete(String alias);

    /**
     * Checks for the existence of a pap.
     * 
     * @param alias identifier of the pap.
     * @return <code>true</code> if the pap exists, <code>false</code> otherwise.
     */
    public boolean exists(String alias);

    /**
     * Returns a the <code>Pap</code> identified by the alias.
     * 
     * @param alias identifier of the pap.
     * @return the <code>Pap</code> identified by <code>alias</code>.
     * 
     * @throws NotFoundException if the given <code>alias</code> were not found.
     */
    public Pap get(String alias);

    /**
     * Returns the alias list of all the defined paps.
     * 
     * @return {@code List<String>} of aliases.
     */
    public List<String> getAliasList();

    /**
     * Returns all the defined paps.
     * 
     * @return a {@code List<Pap>} containing all the defined paps.
     */
    public List<Pap> getAll();

    /**
     * Returns the version of the repository.
     * 
     * @return the version of the repository as <code>String</code>. The version is an integer
     *         number, higher that number is and more recent is the version of the repository.
     */
    public String getVersion();

    /**
     * Stores a pap in the repository.
     * 
     * @param pap the <code>Pap</code> to store.
     * 
     * @throws AlreadyExistsException if a <code>pap</code> with the same <code>alias</code> already
     *             exists.
     * @throws RepositoryException if the pap couldn't be stored for some reason (reason written in
     *             the message of the exception).
     */
    public void store(Pap pap);

    /**
     * Updates the information associated to a <code>pap</code> in the repository.
     * <p>
     * The element to be replaced is the one with the same <code>alias</code> of the given
     * <code>pap</code>.
     * 
     * @param pap the <code>Pap</code> object replacing the one with the same <code>alias</code>.
     * 
     * @throws NotFoundException if a <code>pap</code> with the same <code>alias</code> of the given
     *             one were not found.
     * @throws RepositoryException if the given <code>pap</code> is <code>null</code> or a problem
     *             were encountered while storing the <code>pap</code> into the repository.
     */
    public void update(Pap pap);

}
