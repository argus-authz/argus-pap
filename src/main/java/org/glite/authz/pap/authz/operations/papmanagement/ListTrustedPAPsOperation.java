package org.glite.authz.pap.authz.operations.papmanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManager;
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
        
        List<Pap> remotePapList = PapManager.getInstance().getAllPaps();
        
        PAPData[] papDataArray = new PAPData[remotePapList.size()];
        
        for (int i=0; i<papDataArray.length; i++) {
            Pap pap = remotePapList.get(i);
            
            PAPData papData = new PAPData();
            
            papData.setAlias(pap.getAlias());
            papData.setType(pap.getTypeAsString());
            papData.setDn(pap.getDn());
            papData.setHostname(pap.getHostname());
            papData.setId(pap.getId());
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
