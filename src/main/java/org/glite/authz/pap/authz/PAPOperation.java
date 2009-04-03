package org.glite.authz.pap.authz;

import java.util.Map;

/** An authorized trusted PAP management operation. **/
public interface PAPOperation<T> {

    /**
     * Executes the PAP mamangement operation 
     * @return T, the return value of the operation
     */
    public T execute();

    /**
     * Returns the required permissions to execute this operation
     * @return a map with the required permissions for this operation
     */
    Map <PAPContext, PAPPermission> getRequiredPermission();

}
