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

package org.glite.authz.pap.authz;

import java.util.Collection;
import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.glite.authz.pap.authz.exceptions.PAPAuthzException;

/**
 * A {@link PAPPermission} describes the set of allowed permissions that can be assigned to a {@link PAPAdmin}
 * in a given {@link PAPContext} and are required to execute authorized operations on the PAP.
 *
 */
public class PAPPermission {

    /**
     * 
     * {@link PermissionFlags} enumerates the set of permission flags currently defined for this PAP 
     *
     */
    public enum PermissionFlags {
        /**
         * This flag is used to authorize local policy read operations
         */
        POLICY_READ_LOCAL,
        /**
         * This flag is used to authorize remote policy read operations
         */
        POLICY_READ_REMOTE,
        /**
         * This flag is used to authorized policy write operations
         */
        POLICY_WRITE,
        /**
         * This flag is used to authorize pap configuration (ie, authorization & distribution) read operations
         */
        CONFIGURATION_READ,
        /**
         * This flag is used to authorize pap configuration (ie, authorization & distribution) write operations
         */
        CONFIGURATION_WRITE
    }

    /** 
     * The {@link EnumSet} corresponding to the {@link PermissionFlags} enumeration.
     */
    private EnumSet <PermissionFlags> permissions;

    /**
     * Initializes the permissions as empty.
     */
    protected PAPPermission() {

        permissions = EnumSet.noneOf( PermissionFlags.class );

    }

    /**
     * Creates a {@link PAPPermission} object starting from a string array,
     * like 
     * <code>
     * String[] perms = new String[]{"POLICY_READ_LOCAL","POLICY_READ_REMOTE"};
     * </code>
     * 
     * @param perms, a string array of pap permission flags 
     * @return the {@link PAPPermission} object corresponding to the string array passed as argument
     */
    public static PAPPermission fromStringArray( String[] perms ) {

        PAPPermission perm = new PAPPermission();

        for ( String s : perms ) {

            if ( "ALL".equals( s ) ) {

                perm.permissions
                        .addAll( EnumSet.allOf( PermissionFlags.class ) );
                break;

            } else {
                try {

                    PermissionFlags newPerm = PermissionFlags.valueOf( s.trim() );
                    perm.permissions.add( newPerm );
                } catch ( IllegalArgumentException e ) {

                    throw new PAPAuthzException(
                            "Unknown permission passed as argument! '" + s
                                    + "'.", e );
                }

            }

        }
        return perm;

    }
    
    /**
     * Creates a {@link PAPPermission} object starting from a | separated string of
     * permissions, like:
     * 
     * <code>
     * String perms = "POLICY_READ_LOCAL|POLICY_READ_REMOTE";
     * </code>
     * 
     * 
     * @param s, a | separated string of pap permission flags
     * @return the {@link PAPPermission} object corresponding to the string passed as argument
     */
    public static PAPPermission fromString( String s ) {

        String[] perms = StringUtils.split( s, "|" );

        if ( perms.length == 1 && perms[0].equals( "" ) ) {

            // Return empty permissions by default
            PAPPermission perm = new PAPPermission();
            return perm;
        }

        return fromStringArray( perms );
    }

    public boolean satisfies( PAPPermission other ) {

        return this.permissions.containsAll( other.permissions );
    }

    @Override
    public String toString() {

        if ( this.permissions.containsAll( EnumSet
                .allOf( PermissionFlags.class ) ) )
            return "ALL";

        return StringUtils.join( permissions.iterator(), "|" );

    }

    /**
     * Checks whether this {@link PAPPermission} has the permission flags
     * passed as argument.
     * 
     * @param perm, the permission flags to check
     * @return
     */
    public boolean has( PermissionFlags perm ) {

        return permissions.contains( perm );
    }

    /**
     * Checks whether this {@link PAPPermission} has all the permission flags
     * in the {@link EnumSet} passed as argument
     * @param perms, the set of permission flags to check
     * @return
     */
    public boolean hasAll( EnumSet <PermissionFlags> perms ) {

        return permissions.containsAll( perms );
    }

    /**
     * Returns a string array representation of this {@link PAPPermission} object.
     * @return
     */
    public String[] toStringArray() {

        String[] perms = new String[permissions.size()];
        int i = 0;

        for ( PermissionFlags p : permissions )
            perms[i++] = p.name();

        return perms;

    }

    /**
     * Creates a {@link PAPPermission} object where all the permission flags are set
     * @return
     */
    public static PAPPermission getAllPermission() {

        return PAPPermission.fromString( "ALL" );
    }

    /**
     * Creates a {@link PAPPermission} object where no permission flags are set
     * @return
     */
    public static PAPPermission getEmptyPermission() {

        return new PAPPermission();
    }

    /**
     * Adds all the permission flags in the {@link PAPPermission} passed as argument
     * to the current permission
     * @param o, the {@link PAPPermission} object
     * @return <true> if the permission set has changed as a result of this call, <false> otherwise.
     */
    public boolean add( PAPPermission o ) {

        if ( o == null )
            return false;

        return addAll( o.permissions );
    }

    /**
     * Adds all the permission flags in the collection passed as argument to
     * the current permission's permission flags.
     * 
     * @param c, the permission flag collection to be added
     * @return <true> if the permission set has changed as a result of this call, <false> otherwise.
     */
    protected boolean addAll( Collection <? extends PermissionFlags> c ) {

        return permissions.addAll( c );
    }

    /**
     * Creates a {@link PAPPermission} with the specified permission flag.
     * @param p, the permission flag  that will be added
     * @return a {@link PAPPermission} object with the above permission flag set.
     */
    public static PAPPermission of( PermissionFlags p ) {

        PAPPermission papPerm = new PAPPermission();
        papPerm.permissions.add( p );
        return papPerm;
    }

    /**
     * 
     * Creates a {@link PAPPermission} with the specified permission flags.
     * @param first, the first permission flag that will be added
     * @param second, the second permission flag that will be added.
     * @return a {@link PAPPermission} object with the above permission flags set.
     */
    public static PAPPermission of( PermissionFlags first,
            PermissionFlags second ) {

        PAPPermission papPerm = new PAPPermission();
        papPerm.permissions.add( first );
        papPerm.permissions.add( second );

        return papPerm;
    }

    /**
     * Creates a {@link PAPPermission} with the permission flags passed as argument.
     * @param first, the permission flag  that will be added
     * @param flags, a vararg array of permission flags that will be added to the {@link PAPPermission}
     * @return a {@link PAPPermission} object with the above permission flags set.
     */
    public static PAPPermission of( PermissionFlags first,
            PermissionFlags... flags ) {

        PAPPermission papPerm = new PAPPermission();

        papPerm.permissions.add( first );

        for ( PermissionFlags p : flags )
            papPerm.permissions.add( p );

        return papPerm;
    }
    
}
