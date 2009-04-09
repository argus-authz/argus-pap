package org.glite.authz.pap.authz.operations.papmanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManager;


public class ListPapsOperation extends BasePAPOperation<Pap[]> {

    
    
    protected ListPapsOperation() {

        // TODO Auto-generated constructor stub
    }
    
    
    public static ListPapsOperation instance() {

        return new ListPapsOperation();
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
