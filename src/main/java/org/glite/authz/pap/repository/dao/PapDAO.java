package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.Pap;

public interface PapDAO {

    public void delete(String papAlias);

    public boolean exists(String papAlias);

    public Pap get(String papAlias);

    public List<Pap> getAll();
    
    public String[] getAllAliases();
    
    public String getVersion();
    
    public void store(Pap pap);

    public void update(Pap pap);

}
