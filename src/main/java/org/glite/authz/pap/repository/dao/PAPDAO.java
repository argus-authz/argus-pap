package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public interface PAPDAO {

    public void deleteByAlias(String papAlias) throws NotFoundException, RepositoryException;
    
    public void deleteById(String papId) throws NotFoundException, RepositoryException;

    public List<PAP> getAll();
    
    public List<String> getAllAliases();

    public PAP getByAlias(String papAlias) throws NotFoundException;
    
    public PAP getById(String papId) throws NotFoundException;

    public boolean papExistsByAlias(String papAlias);
    
    public boolean papExistsById(String papId);
    
    public void store(PAP pap) throws AlreadyExistsException, RepositoryException;

    public void update(PAP pap) throws NotFoundException, RepositoryException;
    
}
