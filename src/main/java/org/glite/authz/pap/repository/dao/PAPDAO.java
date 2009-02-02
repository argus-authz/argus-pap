package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public interface PAPDAO {

    public void delete(String papAlias) throws NotFoundException, RepositoryException;
    
    public List<PAP> getAll();
    
    public List<String> getAllAliases();

    public PAP get(String papAlias) throws NotFoundException;
    
    public boolean exists(String papAlias);
    
    public void store(PAP pap) throws AlreadyExistsException, RepositoryException;

    public void update(PAP pap) throws NotFoundException, RepositoryException;
    
}
