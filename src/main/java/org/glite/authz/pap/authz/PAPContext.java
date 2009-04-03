package org.glite.authz.pap.authz;

/**
 * A PAP authorization context. Each authorization context
 * has an associated {@link ACL} describing who is allowed
 * to do certain operations in such context.
 */
public class PAPContext {

    /** The name of this context **/
    String name;

    /** The ACL of this context **/
    ACL acl;

    /**
     * Constructor.
     * @param name, the name for this context
     */
    private PAPContext( String name ) {

        this.name = name;
        acl = new ACL();

    }

    /**
     * Returns the name for this context
     * @return
     */
    public String getName() {

        return name;
    }

    /**
     * Sets the name for this context
     * @param name
     */
    public void setName( String name ) {

        this.name = name;
    }

    /**
     * Returns the ACL for this context
     * @return
     */
    public ACL getAcl() {

        return acl;
    }

    /**
     * Sets the ACL for this context
     * @param acl
     */
    public void setAcl( ACL acl ) {

        this.acl = acl;
    }

    /**
     * Creates a context with the name passed as argument
     * 
     * @param name
     * @return
     */
    public static PAPContext instance( String name ) {

        return new PAPContext( name );
    }

    @Override
    /** **/
    public String toString() {
    
        return name;
    }
}
