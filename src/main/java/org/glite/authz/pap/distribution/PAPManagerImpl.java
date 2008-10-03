package org.glite.authz.pap.distribution;

import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;

public class PAPManagerImpl extends PAPManager {

    @Override
    public PAPContainer add(PAP pap) {
        if (exists(pap.getPapId()))
            throw new AlreadyExistsException();
        distributionConfiguration.setPAP(pap);
        papList.add(pap);
        return null;
    }

    @Override
    public PAP delete(String papId) {
        PAP pap = get(papId);
        papList.remove(pap);
        getContainer(papId).erasePAP();
        return pap;
    }

    @Override
    public boolean exists(String papId) {
        for (PAP pap:papList) {
            if (pap.getPapId().equals(papId))
                return true;
        }
        return false;
    }

    @Override
    public PAP get(String papId) {
        
        for (PAP pap : papList) {
            if (pap.getPapId().equals(papId))
                return pap;
        }
        
        throw new NotFoundException("PAP not found: " + papId);
    }

    @Override
    public List<PAP> getAll() {
        return new ArrayList<PAP>(papList);
    }

    @Override
    public PAPContainer getContainer(String papId) {
        return new PAPContainer(get(papId));
    }

    @Override
    public List<PAPContainer> getContainerAll() {
        List<PAPContainer> papContainerList = new ArrayList<PAPContainer>(papList.size());
        for (PAP pap:papList) {
            papContainerList.add(new PAPContainer(pap));
        }
        return papContainerList;
    }

    @Override
    public void setPAPOrder(List<String> papIdList) {
    // TODO Auto-generated method stub

    }

    @Override
    public void update(String papId, PAP newpap) {
        for (int i=0; i<papList.size(); i++) {
            PAP pap = papList.get(i);
            if (pap.getPapId().equals(papId)) {
                papList.set(i, newpap);
                break;
            }
        }
    }

}
