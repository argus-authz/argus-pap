package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;


public class ListTrustedPAPsOperation extends BasePAPOperation<PAPData[]> {

    
    
    protected ListTrustedPAPsOperation() {

        // TODO Auto-generated constructor stub
    }
    
    
    public static ListTrustedPAPsOperation instance() {

        return new ListTrustedPAPsOperation();
    }
    
    
    @Override
    protected PAPData[] doExecute() {
        
        PAP[] papArray = PAPManager.getInstance().getOrderedRemotePAPsArray();
        
        PAPData[] papDataArray = new PAPData[papArray.length];
        
        for (int i=0; i<papDataArray.length; i++) {
            PAP pap = papArray[i];
            
            PAPData papData = new PAPData();
            
            papData.setAlias(pap.getAlias());
            papData.setDn(pap.getDn());
            papData.setHostname(pap.getHostname());
            papData.setId(pap.getPapId());
            papData.setPath(pap.getPath());
            papData.setPort(pap.getPort());
            papData.setProtocol(pap.getProtocol());
            papData.setVisibilityPublic(pap.isVisibilityPublic());
            
            papDataArray[i] = papData;
        }

        return papDataArray;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );

    }

}
