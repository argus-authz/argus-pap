package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.PAP;

public interface PAPDAO {

    public void deleteByAlias(String papAlias);
    
    public void deleteById(String papId);

    public List<PAP> getAll();
    
    public List<String> getAllAliases();

    public PAP getById(String papId);

    public boolean papExistsByAlias(String papAlias);
    
    public boolean papExistsById(String papId);
    
    // public void setOrder(List<String> papId);
    
    public void store(PAP pap);

    public void update(PAP pap);
    
}
