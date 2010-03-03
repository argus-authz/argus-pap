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

package test.authz;

import java.util.EnumSet;

import junit.framework.TestCase;

import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermissionList;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.authz.exceptions.PAPAuthzException;


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
        
        }catch (PAPAuthzException e) {
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
        
        }catch (PAPAuthzException e) {
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
    
    
    public void testCumulativePerms(){
    	
    	String perm1 = "POLICY_READ_REMOTE";
    	String perm2 = "POLICY_READ_LOCAL";
    	
    	EnumSet<PermissionFlags> perms = EnumSet.of(PermissionFlags.POLICY_READ_LOCAL,PermissionFlags.POLICY_READ_REMOTE);
    	EnumSet<PermissionFlags> compPerms = EnumSet.complementOf(perms);
    	
    	PAPPermissionList l = PAPPermissionList.instance();
    	l.addPermission(PAPPermission.fromString(perm1));
    	l.addPermission(PAPPermission.fromString(perm2));
    	
    	assertTrue(l.getCumulativePermission().hasAll(perms));
    	assertFalse(l.getCumulativePermission().hasAll(compPerms));
    	
    	
    }
    
    public void testEmptyCumulativePerms(){
    	
    	EnumSet<PermissionFlags> allPerms = EnumSet.allOf(PermissionFlags.class);
    	
    	PAPPermissionList l = PAPPermissionList.instance();
    	l.addPermission(PAPPermission.getEmptyPermission());
    	l.addPermission(PAPPermission.getEmptyPermission());
    	
    	for (PermissionFlags f: allPerms){
    		
    		assertFalse(l.getCumulativePermission().has(f));
    		
    	}
    }
    
    public void testPermissionListSatisfy(){
    	
    	String perm1s = "POLICY_READ_LOCAL|POLICY_READ_REMOTE";
    	PAPPermission p = PAPPermission.fromString(perm1s);
    	
    	PAPPermissionList l = PAPPermissionList.instance();
    	
    	l.addPermission(PAPPermission.fromString("POLICY_READ_LOCAL"));
    	l.addPermission(PAPPermission.fromString("POLICY_READ_REMOTE"));
    	
    	assertTrue(l.satisfies(p));
    	
    }
    
}
