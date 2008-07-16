package org.glite.authz.pap.authz;

import java.util.Map;

public interface PAPOperation<T> {

    public T execute();

    Map<PAPContext, PAPPermission> getRequiredPermission();

}
