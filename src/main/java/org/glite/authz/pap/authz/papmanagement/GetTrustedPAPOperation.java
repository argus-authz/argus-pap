package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;


public class GetTrustedPAPOperation extends BasePAPOperation<PAPData> {

    String papId;
    
    protected GetTrustedPAPOperation(String papId){
        
        this.papId = papId;
    }
    
    public static GetTrustedPAPOperation instance(String papId) {

        return new GetTrustedPAPOperation(papId);
    }
    
    @Override
    protected PAPData doExecute() {
        
        PAP pap = PAPManager.getInstance().getPAP( papId );
        
        PAPData papData = new PAPData();
        
        papData.setAlias(pap.getAlias());
        papData.setType(pap.getTypeAsString());
        papData.setDn(pap.getDn());
        papData.setHostname(pap.getHostname());
        papData.setId(pap.getPapId());
        papData.setPath(pap.getPath());
        papData.setPort(pap.getPort());
        papData.setProtocol(pap.getProtocol());
        papData.setVisibilityPublic(pap.isVisibilityPublic());

        return papData;
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );

    }

}
