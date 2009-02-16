package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.PAP;

public interface PAPDAO {

    public void delete(String papAlias);
    
    public List<PAP> getAll();
    
    public String[] getAllAliases();

    public PAP get(String papAlias);
    
    public boolean exists(String papAlias);
    
    public void store(PAP pap);

    public void update(PAP pap);
    
}
