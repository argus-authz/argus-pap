package org.glite.authz.pap.authz;

import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;

public class PAPPermission {

    public enum PermissionFlags {
	POLICY_READ_LOCAL, POLICY_READ_REMOTE, POLICY_WRITE, CONFIGURATION_READ, CONFIGURATION_WRITE
    }

    private EnumSet<PermissionFlags> permissions;

    protected PAPPermission() {

	permissions = EnumSet.noneOf(PermissionFlags.class);

    }

    public static PAPPermission fromStringArray(String[] perms) {

	PAPPermission perm = new PAPPermission();

	for (String s : perms) {

	    if ("ALL".equals(s)) {

		perm.permissions.addAll(EnumSet.allOf(PermissionFlags.class));
		break;

	    } else
		perm.permissions.add(PermissionFlags.valueOf(s));

	}
	return perm;

    }

    public static PAPPermission fromString(String s) {

	String[] perms = StringUtils.split(s, "|");

	if (perms.length == 1 && perms[0].equals("")) {

	    // Return empty permissions by default
	    PAPPermission perm = new PAPPermission();
	    return perm;
	}

	return fromStringArray(perms);
    }

    public boolean satisfies(PAPPermission other) {
	return this.permissions.containsAll(other.permissions);
    }

    @Override
    public String toString() {
	return StringUtils.join(permissions.iterator(), "|");

    }

    public boolean has(PermissionFlags perm) {

	return permissions.contains(perm);
    }

    public boolean hasAll(EnumSet<PermissionFlags> perms) {

	return permissions.containsAll(perms);
    }

    public static PAPPermission getAllPermission() {
	return PAPPermission.fromString("ALL");
    }

    public static PAPPermission of(PermissionFlags p) {

	PAPPermission papPerm = new PAPPermission();
	papPerm.permissions.add(p);
	return papPerm;
    }

    public static PAPPermission of(PermissionFlags first, PermissionFlags second) {

	PAPPermission papPerm = new PAPPermission();
	papPerm.permissions.add(first);
	papPerm.permissions.add(second);

	return papPerm;
    }

    public static PAPPermission of(PermissionFlags first,
	    PermissionFlags... flags) {

	PAPPermission papPerm = new PAPPermission();

	papPerm.permissions.add(first);

	for (PermissionFlags p : flags)
	    papPerm.permissions.add(p);

	return papPerm;
    }
}
