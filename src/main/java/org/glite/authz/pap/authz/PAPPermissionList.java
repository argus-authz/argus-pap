package org.glite.authz.pap.authz;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * A utility class used to group {@link PAPPermission} in a list.
 * 
 */
public class PAPPermissionList {

    /** 
     * The list of permissions
     */
    protected List <PAPPermission> permissions = new ArrayList <PAPPermission>();

    /**
     * Constructor
     */
    private PAPPermissionList() {

    }

    /**
     * Adds a permission to the list. This method silently ignores null additions.
     * 
     * @param p, the {@link PAPPermission} object to be added to the list
     */
    public void addPermission( PAPPermission p ) {

        // Silently ignore null additions
        if ( p == null )
            return;

        permissions.add( p );

    }

    /**
     * Checks whether this list of permissions satifies a given {@link PAPPermission}, ie.
     * has all the required permission flags.
     * @param other, the {@link PAPPermission} to be checked against.
     * @return <code>true</code> if the list satisfies the {@link PAPPermission} passed as argument, <code>false</code> otherwise.
     *  
     */
    public boolean satisfies( PAPPermission other ) {

        return getCumulativePermission().satisfies( other );

    }

    /**
     * Returns the size this permission list.
     */
    public int size() {

        return permissions.size();
    }

    /**
     * Creates a new {@link PAPPermissionList} instance.
     * @return
     */
    public static PAPPermissionList instance() {

        return new PAPPermissionList();
    }

    @Override
    public String toString() {

        return StringUtils.join( permissions, "," );
    }

    /**
     * Returns the cumulative set of permission for this list.
     * The cumulative set of permissions is computed by creating an
     * empty {@link PAPPermission} and adding all the flags contained
     * in the permission list to it.
     * @return the cumulative set of permission for this list.
     */
    public PAPPermission getCumulativePermission() {

        PAPPermission cumulativePerms = PAPPermission.getEmptyPermission();

        for ( PAPPermission p : permissions )
            cumulativePerms.add( p );

        return cumulativePerms;
    }
}
