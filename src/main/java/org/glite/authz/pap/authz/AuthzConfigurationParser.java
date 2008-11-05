package org.glite.authz.pap.authz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.glite.authz.pap.authz.exceptions.PAPAuthzConfigurationException;
import org.glite.authz.pap.common.exceptions.VOMSSyntaxException;
import org.glite.authz.pap.common.utils.PathNamingScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Andrea Ceccanti
 * 
 */
public final class AuthzConfigurationParser {

    public static final Logger log = LoggerFactory
            .getLogger( AuthzConfigurationParser.class );

    public static final String permissionRegex = "^([^:]+):\\s*((\\w\\|?)+)";

    public static final Pattern permissionPattern = Pattern
            .compile( permissionRegex );

    public static final String anyUserRegex = "^ANYONE\\s*";

    public static final Pattern anyUserPattern = Pattern.compile( anyUserRegex );

    public static final String dnRegex = "^\"((/[^=]+=([^/]|\\s)+)+)\"\\s*";

    public static final Pattern dnPattern = Pattern.compile( dnRegex );

    public static final String emptyLineRegex = "\\s*$";

    public static final Pattern emptyLinePattern = Pattern
            .compile( emptyLineRegex );

    public static final String commentRegex = "^#.*$";

    public static final Pattern commentPattern = Pattern.compile( commentRegex );

    public static final String stanzaRegex = "^(\\[\\w+\\])\\s*$";

    public static final Pattern stanzaPattern = Pattern.compile( stanzaRegex );

    enum Sections {
        DN, FQAN
    }

    EnumMap <Sections, String> sectionNames = new EnumMap <Sections, String>(
            Sections.class );

    enum ParserStates {
        DNs, FQANs, UNDEFINED
    }

    private ACL globalContextACL;

    private ParserStates state = ParserStates.UNDEFINED;

    private int lineCounter = 0;

    private void init() {

        // Initialize sectionNames map
        sectionNames.put( Sections.DN, "[dn]" );
        sectionNames.put( Sections.FQAN, "[fqan]" );

        // Initialize ACL
        globalContextACL = new ACL();

    }

    private AuthzConfigurationParser() {

        init();
    }

    protected void parseLine( String line ) {

        if ( line.length() == 0 )
            return;

        Matcher stanzaMatcher = stanzaPattern.matcher( line );
        Matcher commentMatcher = commentPattern.matcher( line );
        Matcher emptyLineMatcher = emptyLinePattern.matcher( line );
        Matcher permissionMatcher = permissionPattern.matcher( line );

        if ( commentMatcher.matches() )
            return;

        if ( emptyLineMatcher.matches() )
            return;

        if ( stanzaMatcher.matches() ) {
            String stanzaName = stanzaMatcher.group();

            if ( !sectionNames.values().contains( stanzaName ) )
                throw new PAPAuthzConfigurationException( "Stanza '"
                        + stanzaName + "' is not supported!" );

            if ( "[dn]".equals( stanzaName ) ) {
                state = ParserStates.DNs;

            } else if ( "[fqan]".equals( stanzaName ) ) {
                state = ParserStates.FQANs;
            }

            return;
        }

        if ( permissionMatcher.matches() ) {

            String principalName = permissionMatcher.group( 1 );
            String permissions = permissionMatcher.group( 2 );

            PAPPermission perm = PAPPermission.fromString( permissions );

            Matcher dnMatcher = dnPattern.matcher( principalName );
            Matcher anyUserMatcher = anyUserPattern.matcher( principalName );

            if ( anyUserMatcher.matches() ) {

                if ( !state.equals( ParserStates.DNs ) )
                    throw new PAPAuthzConfigurationException(
                            "Found an X509 ANYONE declaration outside of the [dn] stanza!" );

                PAPAdmin admin = PAPAdminFactory.getAnyAuthenticatedUserAdmin();
                globalContextACL.setPermissions( admin, perm );

            } else if ( dnMatcher.matches() ) {

                if ( !state.equals( ParserStates.DNs ) )
                    throw new PAPAuthzConfigurationException(
                            "Found an X509 DN outside of the [dn] stanza!" );

                String dn = dnMatcher.group( 1 );
                PAPAdmin admin = PAPAdminFactory.getDn( dn );

                globalContextACL.setPermissions( admin, perm );

            } else {

                // Check if the Principal is a VOMS FQAN
                try {
                    principalName = principalName.trim();
                    PathNamingScheme.checkSyntax( principalName );

                    if ( !state.equals( ParserStates.FQANs ) )
                        throw new PAPAuthzConfigurationException(
                                "Found a VOMS FQAN outside the [fqan] stanza!" );

                    PAPAdmin admin = PAPAdminFactory.getFQAN( principalName );
                    globalContextACL.setPermissions( admin, perm );

                } catch ( VOMSSyntaxException e ) {
                    throw new PAPAuthzConfigurationException(
                            "Unsupported principal name: '" + principalName
                                    + "'." );
                }

            }
        } else
            throw new PAPAuthzConfigurationException( "Syntax error at line "
                    + lineCounter + ": '" + line
                    + "' does not match the PRINCIPAL : PERMISSION format!" );
    }

    public ACL parse( File f ) {

        try {

            BufferedReader reader = new BufferedReader( new FileReader( f ) );

            String line = null;
            lineCounter = 0;

            do {

                line = reader.readLine();
                if ( line != null ) {
                    lineCounter++;
                    parseLine( line );
                }

            } while ( line != null );

            return globalContextACL;

        } catch ( IOException e ) {

            throw new PAPAuthzConfigurationException( e );
        }

    }

    public void save( File f, ACL globalContextACL ) {

        try {
        
            PrintWriter writer = new PrintWriter(f);
            
            writer.println("[dn]\n\n");
            
            for (Map.Entry <PAPAdmin, PAPPermission> entry: globalContextACL.getPermissions().entrySet()){
                
                if (entry.getKey() instanceof X509Principal){
                    
                    X509Principal p = (X509Principal)entry.getKey();
                    
                    String dn = null;
                    
                    if (p.equals( PAPAdminFactory.getAnyAuthenticatedUserAdmin() ))
                        dn = "ANYONE";
                    else
                        dn = "\""+p.getDn()+"\"";
                        
                    writer.println(dn+" : "+entry.getValue().toString());
                    
                }
            }
            
            writer.println("\n\n[fqan]\n\n");
            
            for (Map.Entry <PAPAdmin, PAPPermission> entry: globalContextACL.getPermissions().entrySet()){
                
                if (entry.getKey() instanceof VOMSFQAN){
                    
                    VOMSFQAN p = (VOMSFQAN)entry.getKey();
                    writer.println(p.getFqan()+" : "+entry.getValue().toString());
                    
                }
            }
            
            writer.flush();
            writer.close();
            
        } catch ( IOException e ) {
            
            log.error( "Error writing authz configuration to file: "+e.getMessage(),e );
            throw new PAPAuthzConfigurationException("Error writing authz configuration to file: "+e.getMessage(),e );
            
        } 
        
        
    }

    public ACL getParsedACL() {

        return globalContextACL;
    }

    public static AuthzConfigurationParser instance() {

        return new AuthzConfigurationParser();
    }

    public static void main( String[] args ) {

        AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
        parser.parse( new File( "/home/andrea/test_authz_conf.txt" ) );

        log.info( parser.getParsedACL().toString() );

    }
}
