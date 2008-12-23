package org.glite.authz.pap.authz.papmanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;


public class ListTrustedPAPsOperation extends BasePAPOperation<List <PAP>> {

    
    
    protected ListTrustedPAPsOperation() {

        // TODO Auto-generated constructor stub
    }
    
    
    public static ListTrustedPAPsOperation instance() {

        return new ListTrustedPAPsOperation();
    }
    
    
    @Override
    protected List<PAP> doExecute() {

        return PAPManager.getInstance().getAllTrustedPAPs();
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );

    }

}
