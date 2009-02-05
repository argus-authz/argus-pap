package org.glite.authz.pap.authz;

public class VOMSFQAN extends BasePAPAdmin {

    String fqan;

    public VOMSFQAN( String fqan ) {

        this.fqan = fqan;
    }

    public String getName() {

        return getFqan();
    }

    public String getFqan() {

        return fqan;
    }

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
