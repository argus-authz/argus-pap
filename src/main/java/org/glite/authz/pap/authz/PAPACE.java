package org.glite.authz.pap.authz;

public class PAPACE {

    PAPContext context;

    PAPAdmin admin;

    PAPPermission perms;

    private PAPACE( PAPContext context, PAPAdmin admin, PAPPermission perms ) {

        this.context = context;
        this.admin = admin;
        this.perms = perms;

    }

    public static PAPACE instance( PAPContext context, PAPAdmin admin ) {

        return new PAPACE( context, admin, null );
    }

    public static PAPACE instance( PAPContext context, PAPAdmin admin,
            PAPPermission perms ) {

        return new PAPACE( context, admin, perms );
    }

    public PAPContext getContext() {

        return context;
    }

    public void setContext( PAPContext context ) {

        this.context = context;
    }

    public PAPAdmin getAdmin() {

        return admin;
    }

    public void setAdmin( PAPAdmin admin ) {

        this.admin = admin;
    }

    public PAPPermission getPerms() {

        return perms;
    }

    public void setPerms( PAPPermission perms ) {

        this.perms = perms;
    }

}
