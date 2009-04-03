package org.glite.authz.pap.authz;

/**
 * 
 * A VOMS FQAN principal.
 *
 */
public class VOMSFQAN extends BasePAPAdmin {
    
    /**
     * The fqan string of this {@link VOMSFQAN} principal.
     */
    String fqan;

    /**
     * Constructor.
     * 
     * @param fqan, the fqan string for this {@link VOMSFQAN} principal. 
     */
    public VOMSFQAN( String fqan ) {

        this.fqan = fqan;
    }

    /**
     * Returns the name of this {@link VOMSFQAN} principal, ie: the 
     * fqan string. Actually this method calls {@link #getFqan()}.
     * 
     * @return a string containing the voms fqan associated with this
     * {@link VOMSFQAN} principal 
     */
    public String getName() {

        return getFqan();
    }

    /**
     * Returns the fqan string for this {@link VOMSFQAN} principal.
     * @return a string containing the voms fqan associated with this
     * {@link VOMSFQAN} principal 
     */
    public String getFqan() {

        return fqan;
    }

    /**
     * Sets the fqan string for this {@link VOMSFQAN} principal.
     * @param fqan
     */
    public void setFqan( String fqan ) {

        this.fqan = fqan;
    }

    @Override
    public String toString() {

        return "[fqan]=" + getFqan();
    }

    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof VOMSFQAN ) )
            return false;

        VOMSFQAN other = (VOMSFQAN) obj;

        return fqan.equals( other.fqan );
    }

    @Override
    public int hashCode() {

        if ( fqan == null )
            return 1;

        return fqan.hashCode();

    }

}
