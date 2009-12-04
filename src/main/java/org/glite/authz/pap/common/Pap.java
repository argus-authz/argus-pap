package org.glite.authz.pap.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.wizard.WizardUtils;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>Pap</code> class is a JavaBean describing the information associated to a pap.
 * <p>
 * The PAP service (PAP uppercase) is organized in units and each unit is called pap (pap
 * lowercase). Basically a pap is a container for policies. This class models the information
 * associated to a pap and not its content (for managing the policies of a pap see
 * {@link PapContainer}.
 * <p>
 * A pap can be local or remote. The policies of a local pap are written by the administrator of the
 * PAP service, on the other side the policies of a remote pap are retrieved remotely (downloaded
 * from the owner PAP). Information like hostname, port, protocol and path are used to build the
 * endpoint of a remote pap. A pap can also be private or public. When a PAP requests policies to
 * another PAP, the policies that are actually sent are all the public policies belonging to a
 * public pap. Finally a pap can be enabled or disabled. If it's enabled then its policies are sent
 * to PDPs, otherwise they are not. By default a pap is not enabled.
 * 
 * @see PapManager
 * @see PapContainer
 * 
 */

@Entity
@Table(name = "pap")
public class Pap {

    /** Default host: {@value} */
    public static final String DEFAULT_HOST = "localhost";
    /** Alias of the default pap: {@value} */
    public static final String DEFAULT_PAP_ALIAS = "default";
    /** Default port: {@value} */
    public static final String DEFAULT_PORT = "8150";
    /** Default protocol: {@value} */
    public static final String DEFAULT_PROTOCOL = "https";
    /** Default service path: {@value} */
    public static final String DEFAULT_SERVICES_ROOT_PATH = "/" + PAPConfiguration.DEFAULT_WEBAPP_CONTEXT
            + "/services/";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(Pap.class);
    
    @Id
    @Column(name = "id")
    private String id = null;
    
    @Column(name = "alias", unique = true)
    private String alias = null;
    
    @Column(name = "dn")
    private String dn = null;
    
    @Column(name = "hostname")
    private String hostname = null;
    
    @Column(name = "path")
    private String path = null;
    
    @Column(name = "policy_last_modification_at")
    private long policyLastModificationTimeInMillis = 0;
    
    @Column(name = "port")
    private String port = null;
    
    @Column(name = "protocol")
    private String protocol = null;
    
    @Column(name = "is_local")
    private boolean local = true;
    
    @Column(name = "is_public")
    private boolean visibilityPublic = false;
    
    @Column(name = "is_enabled")
    private boolean enabled = false;

    /**
     * Constructor with no parameters. Needed for JavaBean compatibility and used by Axis
     * serialization methods.
     */
    public Pap() {}

    /**
     * Constructor.
     * 
     * @param alias alias of the pap (it must be a unique name).
     * @param isLocal if <code>true</code> the pap is local, if <code>false</code> the pap is
     *            remote.
     * @param dn DN of the pap (remote pap). Can be <code>null</code>.
     * @param hostname hostname of the pap (remote pap). If <code>null</code> or empty the default
     *            value is {@link #DEFAULT_HOST}.
     * @param port port port of the pap (remote pap). If <code>null</code> or empty the default
     *            value is {@link #DEFAULT_PORT}.
     * @param servicesRootPath service path of the pap (remote pap). If <code>null</code> or empty
     *            the default value is {@link #DEFAULT_SERVICES_ROOT_PATH}.
     * @param protocol protocol of the pap (remote pap). If <code>null</code> or empty the default
     *            value is {@link #DEFAULT_PROTOCOL}.
     * @param visibilityPublic if <code>true</code> the pap is public, if <code>false</code> the pap
     *            is private.
     */
    public Pap(String alias, boolean isLocal, String dn, String hostname, String port,
            String servicesRootPath, String protocol, boolean visibilityPublic) {

        assert (alias != null) && (alias.length() > 0) : "alias cannot be null or empty";

        this.alias = alias;
        this.visibilityPublic = visibilityPublic;
        this.local = isLocal;
        this.dn = dn;

        id = WizardUtils.generateId(alias);

        if (Utils.isDefined(hostname)) {
            this.hostname = hostname;
        } else {
            this.hostname = DEFAULT_HOST;
        }

        if (Utils.isDefined(port)) {
            this.port = port;
        } else {
            this.port = DEFAULT_PORT;
        }

        if (Utils.isDefined(servicesRootPath)) {
            this.path = servicesRootPath;
        } else {
            this.path = DEFAULT_SERVICES_ROOT_PATH;
        }

        if (Utils.isDefined(protocol)) {
            this.protocol = protocol;
        } else {
            this.protocol = DEFAULT_PROTOCOL;
        }
    }

    /**
     * Return <code>true</code> if the given string equals "local" (not case sentitive).<br>
     * Convenience method used for reading the type from configuration file.
     * 
     * @param type a <code>String</code> to be compared with the <code>Strinf</code> "local".
     * @return <code>true</code> if <code>type</code> is equal to "local" (not case sensitive),
     *         <code>false</code> otherwise.
     */
    public static boolean isLocal(String type) {
        if ("local".equals(type.toLowerCase())) {
            return true;
        }
        return false;
    }

    /**
     * Constructor for the <i>default</i> pap.
     * <p>
     * The default pap alias is defined by {@link Pap#DEFAULT_PAP_ALIAS}.
     * 
     * @return the default pap (alias={@value #DEFAULT_PAP_ALIAS}, local and public)).
     */
    public static Pap makeDefaultPAP() {
        
        Pap defaultPap = new Pap(DEFAULT_PAP_ALIAS, true, null, null, null, null, null, true);
        defaultPap.setEnabled(true);
        
        return defaultPap;
    }

    /**
     * Compares this <code>Pap</code> to the specified object. The result is <code>true</code> if
     * and only if the argument is not <code>null</code> and is a <code>Pap</code> object whose
     * members value is the same as this object.
     * 
     * @param pap
     * @return
     */
    public boolean equals(Pap pap) {

        if (pap == null) {
            return false;
        }

        if (!alias.equals(pap.getAlias())) {
            return false;
        }

        if (!(local == pap.isLocal())) {
            return false;
        }

        if (!(visibilityPublic == pap.isVisibilityPublic())) {
            return false;
        }

        if (dn != null) {
            if (!dn.equals(pap.getDn())) {
                return false;
            }
        } else if (pap.getDn() != null) {
            return false;
        }

        if (hostname != null) {
            if (!hostname.equals(pap.getHostname())) {
                return false;
            }
        } else if (pap.getHostname() != null) {
            return false;
        }

        if (port != null) {
            if (!port.equals(pap.getPort())) {
                return false;
            }
        } else if (pap.getPort() != null) {
            return false;
        }

        if (path != null) {
            if (!path.equals(pap.getPath())) {
                return false;
            }
        } else if (pap.getPath() != null) {
            return false;
        }

        if (protocol != null) {
            if (!protocol.equals(pap.getProtocol())) {
                return false;
            }
        } else if (pap.getProtocol() != null) {
            return false;
        }

        return true;
    }

    public String getAlias() {
        return alias;
    }

    public String getDn() {
        return dn;
    }

    /**
     * Returns the endpoint of the pap in the form: protocol://hostname:port/path.
     * 
     * @return a <code>String</code> representing the endpoint. No check is performed on protocol,
     *         hostname, port and path. If some of them are <code>null</code> then the corresponding
     *         part of the endpoint string reports a "null" string.
     */
    public String getEndpoint() {
        return protocol + "://" + hostname + ":" + port + path;
    }

    public String getHostname() {
        return hostname;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getPolicyLastModificationTimeInSecondsString() {
        return String.valueOf(policyLastModificationTimeInMillis / 1000);
    }

    public String getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getTypeAsString() {
        if (local) {
            return "local";
        }
        return "remote";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLocal() {
        return local;
    }

    public boolean isRemote() {
        return !local;
    }

    public boolean isVisibilityPublic() {
        return visibilityPublic;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public void setAll(Pap pap) {
        alias = pap.getAlias();
        dn = pap.getDn();
        hostname = pap.getHostname();
        path = pap.getPath();
        port = pap.getPort();
        protocol = pap.getProtocol();
        enabled = pap.isEnabled();
        local = pap.isLocal();
        visibilityPublic = pap.isVisibilityPublic();
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setId(String papId) {
        this.id = papId;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPolicyLastModificationTime(long milliseconds) {
        this.policyLastModificationTimeInMillis = milliseconds;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setPublic(boolean isPublic) {
        this.visibilityPublic = isPublic;
    }

    public void setVisibilityPublic(boolean visibilityPublic) {
        this.visibilityPublic = visibilityPublic;
    }

    public String toFormattedString() {
        return toFormattedString(0, 4);
    }

    public String toFormattedString(int indent) {
        return toFormattedString(indent, 4);
    }

    /**
     * Returns a formatted <code>String</code> version of this <code>Pap</code>. Multiple lines are
     * used and indentation.
     * 
     * @param indent the indentation to be used.
     * @param padding the padding to be used.
     * @return the formatted <code>String</code> representing this <code>Pap</code>.
     */
    public String toFormattedString(int indent, int padding) {

        String indentString = Utils.fillWithSpaces(indent);
        String paddingString = Utils.fillWithSpaces(indent + padding);

        String visibility;
        if (visibilityPublic) {
            visibility = "public";
        } else {
            visibility = "private";
        }

        String aliasString = String.format("%salias=%s\n", indentString, alias);
        String typeString = String.format("%sisLocal=%b\n", paddingString, local);
        String visibilityString = String.format("%svisibility=%s\n", paddingString, visibility);
        String enabledString = String.format("%senabled=%b\n", paddingString, enabled);

        String dnString = String.format("%sdn=%s\n", paddingString, dn);
        String endpointString = String.format("%sendpoint=%s\n", paddingString, getEndpoint());

        if (local) {

            if (dn == null) {
                dnString = "";
            }

            if ((hostname == null) && (port == null)) {
                endpointString = "";
            }
        }

        return aliasString + typeString + visibilityString + enabledString + dnString + endpointString;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String visibility;

        if (visibilityPublic) {
            visibility = "public";
        } else {
            visibility = "private";
        }
        return String.format("alias=\"%s\" isLocal=\"%b\" visibility=\"%s\" enabled=\"%b\" dn=\"%s\" endpoint=\"%s\" id=\"%s\"",
                             alias,
                             local,
                             visibility,
                             enabled,
                             dn,
                             getEndpoint(),
                             id);
    }
}
