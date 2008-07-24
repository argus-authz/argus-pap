package org.glite.authz.pap.repository;

import java.util.List;

import org.glite.authz.pap.common.PAP;

public interface PAPManager {

    public PAPContainer create(PAP pap);

    public void delete(PAP pap);

    public boolean exists(PAP pap);

    public PAPContainer getContainer(PAP pap);

    public List<PAPContainer> getContainerAll();

}
