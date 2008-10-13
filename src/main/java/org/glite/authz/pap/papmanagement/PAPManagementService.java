package org.glite.authz.pap.papmanagement;

import java.util.List;

import org.glite.authz.pap.common.PAP;

public interface PAPManagementService {
    
    public abstract void addTrustedPAP(PAP pap) throws java.rmi.RemoteException;
    
    public abstract boolean exists(String papId) throws java.rmi.RemoteException;
    
    public abstract PAP getTrustedPAP(String papId) throws java.rmi.RemoteException;
    
    public abstract List<PAP> listTrustedPAPs() throws java.rmi.RemoteException;
    
    public abstract String ping() throws java.rmi.RemoteException;
    
    public abstract void removeTrustedPAP(String papId) throws java.rmi.RemoteException;
    
    public abstract void updateTrustedPAP(String papId, PAP newpap) throws java.rmi.RemoteException;

}
