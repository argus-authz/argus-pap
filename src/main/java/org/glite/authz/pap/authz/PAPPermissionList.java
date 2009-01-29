package org.glite.authz.pap.authz;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PAPPermissionList {

	protected List<PAPPermission> permissions = new ArrayList<PAPPermission>();

	private PAPPermissionList() {

	}

	public void addPermission(PAPPermission p) {

		// Silently ignore null additions
		if (p == null)
			return;

		permissions.add(p);

	}

	public boolean satisfies(PAPPermission other) {

		return getCumulativePermission().satisfies(other);
		
	}

	public int size() {

		return permissions.size();
	}

	public static PAPPermissionList instance() {

		return new PAPPermissionList();
	}

	@Override
	public String toString() {

		return ToStringBuilder.reflectionToString(this);
	}
	
	
	public PAPPermission getCumulativePermission(){
		PAPPermission cumulativePerms = PAPPermission.getEmptyPermission();
		
		for (PAPPermission p: permissions)
			cumulativePerms.add(p);
		
		return cumulativePerms;
	}
}
