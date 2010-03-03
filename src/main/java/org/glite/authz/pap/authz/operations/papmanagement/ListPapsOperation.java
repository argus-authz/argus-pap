/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
