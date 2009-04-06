package org.glite.authz.pap.authz.operations.papmanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManager;


public class ListTrustedPAPsOperation extends BasePAPOperation<Pap[]> {

    
    
    protected ListTrustedPAPsOperation() {

        // TODO Auto-generated constructor stub
    }
    
    
    public static ListTrustedPAPsOperation instance() {

        return new ListTrustedPAPsOperation();
    }
    
    
    @Override
    protected Pap[] doExecute() {
        
        List<Pap> remotePapList = PapManager.getInstance().getAllPaps();
        
        Pap[] papArray = new Pap[remotePapList.size()];
        
        for (int i=0; i<papArray.length; i++) {
            papArray[i] = remotePapList.get(i);
        }

        return papArray;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );

    }

}
