package org.glite.authz.pap.common;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.wizard.WizardUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class provides information about a Policy Administration Point (PAP).
 * 
 */
public class Pap {

    public static String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_PAP_ALIAS = "default";
    public static String DEFAULT_PORT = "8150";
    public static String DEFAULT_PROTOCOL = "https";
    public static String DEFAULT_SERVICES_ROOT_PATH = "/" + PAPConfiguration.DEFAULT_WEBAPP_CONTEXT
            + "/services/";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(Pap.class);
    private String alias = null;
    private String dn = null;
    private String hostname = null;
    private String id = null;
    private String path = null;
    private long policyLastModificationTimeInMillis = 0;
    private String port = null;
    private String protocol = null;
    private boolean local = true;
    private boolean visibilityPublic;

    public Pap() {
    }

    public Pap(String alias) {
        this(alias, true, null, null, null, null, null, false);
    }

    public Pap(String alias, boolean visibilityPublic) {
        this(alias, true, null, null, null, null, null, visibilityPublic);
    }

    public Pap(String alias, boolean isLocal, String dn, String hostname, String port,
            String servicesRootPath, boolean visibilityPublic) {
        this(alias, isLocal, dn, hostname, port, servicesRootPath, null, visibilityPublic);
    }

    public Pap(String alias, boolean isLocal, String dn, String hostname, String port,
            String servicesRootPath, String protocol, boolean visibilityPublic) {

        assert (alias != null) && (alias.length() > 0) : "alias cannot be null or empty";

        this.alias = alias;
        this.visibilityPublic = visibilityPublic;
        this.local = isLocal;

        id = WizardUtils.generateId(alias);

        if (!isLocal) {
            this.hostname = DEFAULT_HOST;
            this.port = DEFAULT_PORT;
            this.path = DEFAULT_SERVICES_ROOT_PATH;
            this.protocol = DEFAULT_PROTOCOL;
        }

        if (Utils.isDefined(dn))
            this.dn = dn;
        if (Utils.isDefined(hostname))
            this.hostname = hostname;
        if (Utils.isDefined(port))
            this.port = port;
        if (Utils.isDefined(servicesRootPath))
            this.path = servicesRootPath;
        if (Utils.isDefined(protocol))
            this.protocol = protocol;
    }

    public Pap(String alias, String dn, String hostname) {
        this(alias, dn, hostname, false);
    }

    public Pap(String alias, String dn, String hostname, boolean isPublic) {
        this(alias, false, dn, hostname, null, null, null, isPublic);
    }

    public Pap(String alias, String dn, String hostname, String port, String servicesRootPath,
            boolean visibilityPublic) {
        this(alias, false, dn, hostname, port, servicesRootPath, null, visibilityPublic);
    }

    public static boolean isLocal(String type) {
        if ("local".equals(type.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static Pap makeDefaultPAP() {
        return new Pap(DEFAULT_PAP_ALIAS, true);
    }

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

    public void setDn(String dn) {
        this.dn = dn;
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
     * Returns a formatted version of this PAP
     * 
     * @param indent , the indentation to be used
     * @param padding , the padding to be used
     * @return the formatted String representing this PAP
     * 
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

        return aliasString + typeString + visibilityString + dnString + endpointString;
    }

    @Override
    public String toString() {
        String visibility;

        if (visibilityPublic) {
            visibility = "public";
        } else {
            visibility = "private";
        }
        return String.format("alias=\"%s\" isLocal=\"%b\" visibility=\"%s\" dn=\"%s\" endpoint=\"%s\" id=\"%s\"",
                             alias,
                             local,
                             visibility,
                             dn,
                             getEndpoint(),
                             id);
    }
}
