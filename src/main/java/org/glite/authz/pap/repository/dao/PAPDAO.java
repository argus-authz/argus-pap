package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.PAP;

public interface PAPDAO {

    public void add(PAP pap);

    public void delete(String papId);

    public boolean exists(String papId);

    public List<PAP> getAll();
    
    public PAP getById(String papId);
    
    public void setOrder(List<String> papId);

    public void update(PAP pap);

}
