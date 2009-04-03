package org.glite.authz.pap.authz;
/**
 * 
 * An X509 principal. This class describes a PAP administrator
 * that authenticated using an X509 certificate.
 *
 */
public class X509Principal extends BasePAPAdmin {

    /**
     * The X509 distinguished name of this {@link X509Principal}
     */
    String dn;

    /**
     * Constructor
     * 
     * @param dn, the X509 dn that will be associated with this {@link X509Principal}
     */
    public X509Principal( String dn ) {

        this.dn = dn;

    }

    /**
     * Returns the name of this principal. Actually this method is a
     * Synonym for {@link #getDn()}.
     * 
     */
    public String getName() {

        return getDn();
    }

    /**
     * Returns this principal's X509 distinguished name.
     * @return
     */
    public String getDn() {

        return dn;
    }

    /**
     * Sets this principal's X509 distinguished name.
     * @param dn
     */
    public void setDn( String dn ) {

        this.dn = dn;
    }

    @Override
    public String toString() {

        return "[dn]=" + getDn();
    }

    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof X509Principal ) )
            return false;

        X509Principal that = (X509Principal) obj;

        return this.dn.equals( that.dn );

    }

    @Override
    public int hashCode() {

        if ( dn == null )
            return 1;

        return dn.hashCode();
    }

}
