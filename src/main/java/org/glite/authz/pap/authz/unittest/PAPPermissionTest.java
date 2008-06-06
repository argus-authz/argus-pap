package org.glite.authz.pap.authz.unittest;

import java.util.EnumSet;

import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import junit.framework.TestCase;


public class PAPPermissionTest extends TestCase {

    
    public void testSingleStringParsingSuccess(){
        
        String permission = "POLICY_READ_LOCAL";
        
        EnumSet <PermissionFlags> perms = EnumSet.of( PermissionFlags.POLICY_READ_LOCAL );
        EnumSet <PermissionFlags> otherPerms = EnumSet.complementOf( perms );
        
        
        PAPPermission perm = PAPPermission.fromString( permission );
        assertTrue( perm.has( PermissionFlags.POLICY_READ_LOCAL ));
                        
        for (PermissionFlags f: otherPerms)
            assertFalse( perm.has( f ) );
        
        
    }

    public void testSingleStringParsingFailure(){
        String permission = "POLICY_READ_READ";
        
        boolean caughtException = false;
        
        try{
            PAPPermission perm = PAPPermission.fromString( permission );
        }catch (IllegalArgumentException e) {
            caughtException = true;
        }
        
        assertTrue( "The parsing of an unknown permission type didn't cause an exception!", caughtException );
            
    }
    
    public void testMultipleStringParsingSuccess(){
        
        String permissionList = "POLICY_READ_LOCAL|POLICY_READ_REMOTE";
        EnumSet <PermissionFlags> perms = EnumSet.of( PermissionFlags.POLICY_READ_LOCAL, PermissionFlags.POLICY_READ_REMOTE );
        EnumSet <PermissionFlags> otherPerms = EnumSet.complementOf( perms );
        
        PAPPermission perm = PAPPermission.fromString( permissionList );
        assertTrue(perm.hasAll( perms ));
        
        for (PermissionFlags f: otherPerms)
            assertFalse( perm.has( f ) );
        
    }
    
    public void testMultipleStringParsingFailure(){
        
        String permissionList = "POLICY_READ_LOCAL|%CICCIO%|ALL";
        
        boolean caughtException = false;
        
        try{
            
            PAPPermission perm = PAPPermission.fromString( permissionList );
        
        }catch (IllegalArgumentException e) {
            caughtException = true;
        }
        
        assertTrue( "The parsing of a permission list containing junk didn't cause an exception!", caughtException );
        
    }
    
    public void testAllPermsParsing(){
        
        String permission = "ALL";
        EnumSet <PermissionFlags> perms = EnumSet.allOf( PermissionFlags.class );
        
        PAPPermission perm = PAPPermission.fromString( permission );
        
        assertTrue( perm.hasAll( perms ) );
        
    }
    
    
}
