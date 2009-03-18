package org.glite.authz.pap.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.wizard.WizardUtils;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAP {

    public static enum PSType {
        LOCAL, REMOTE;

        public static PSType get(String type) {
            if ("local".equals(type.toLowerCase())) {
                return LOCAL;
            }
            if ("remote".equals(type.toLowerCase())) {
                return REMOTE;
            }
            return LOCAL;
        }

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static String DEFAULT_DN = "invalid_dn";

    public static String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_PAP_ALIAS = "default";
    public static String DEFAULT_PORT = "8150";
    public static String DEFAULT_PROTOCOL = "https";
    public static String DEFAULT_SERVICES_ROOT_PATH = "/glite-authz-pap/services/";

    private static DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    private static final Logger log = LoggerFactory.getLogger(PAP.class);

    private String alias = null;
    private String dn = null;
    private String hostname = null;
    private String papId = null;
    private String path = null;
    private Date policyLastModificationTime = null;
    private String port = null;
    private String protocol = null;
    private PSType pstype = null;
    private boolean visibilityPublic;

    public PAP(PAPData papData) {
        this(papData.getAlias(), PSType.get(papData.getType()), papData.getDn(), papData.getHostname(), papData.getPort(),
             papData.getPath(), papData.getProtocol(), papData.isVisibilityPublic());
    }

    public PAP(String alias) {
        this(alias, PSType.LOCAL, null, null, null, null, null, false);
    }
    
    public PAP(String alias, boolean visibilityPublic) {
        this(alias, PSType.LOCAL, null, null, null, null, null, visibilityPublic);
    }

    public PAP(String alias, PSType pstype, String dn, String hostname, String port, String servicesRootPath,
            boolean visibilityPublic) {
        this(alias, pstype, dn, hostname, port, servicesRootPath, null, visibilityPublic);
    }

    public PAP(String alias, PSType pstype, String dn, String hostname, String port, String servicesRootPath, String protocol,
            boolean visibilityPublic) {

        assert (alias != null) && (alias.length() > 0) : "alias cannot be null or empty";

        this.alias = alias;
        this.visibilityPublic = visibilityPublic;
        this.pstype = pstype;

        papId = WizardUtils.generateId(alias);

        if (pstype == PSType.REMOTE) {
            this.dn = DEFAULT_DN;
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

    public PAP(String alias, String dn, String hostname) {
        this(alias, dn, hostname, false);
    }

    public PAP(String alias, String dn, String hostname, boolean isPublic) {
        this(alias, PSType.REMOTE, dn, hostname, null, null, null, isPublic);
    }

    public PAP(String alias, String dn, String hostname, String port, String servicesRootPath, boolean visibilityPublic) {
        this(alias, PSType.REMOTE, dn, hostname, port, servicesRootPath, null, visibilityPublic);
    }

    public static PAP makeDefaultPAP() {
        return new PAP(DEFAULT_PAP_ALIAS, true);
    }

    public boolean equals(PAP pap) {

        if (pap == null) {
            return false;
        }
        
        if (!alias.equals(pap.getAlias())) {
            return false;
        }
        
        if (!(pstype == pap.getType())) {
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

    public String getPapId() {
        return papId;
    }

    public String getPath() {
        return path;
    }

    public Date getPolicyLastModificationTime() {
        return policyLastModificationTime;
    }

    public String getPolicyLastModificationTimeString() {

        if (policyLastModificationTime == null) {
            return "Undefined";
        }

        return df.format(policyLastModificationTime);
    }

    public String getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public PSType getType() {
        return pstype;
    }

    public String getTypeAsString() {
        return pstype.toString();
    }

    public boolean isLocal() {
        if (pstype == PSType.LOCAL) {
            return true;
        }
        return false;
    }

    public boolean isRemote() {
        if (pstype == PSType.REMOTE) {
            return true;
        }
        return false;
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

    public void setPapId(String papId) {
        this.papId = papId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPolicyLastModificationTime(Date policyLastModificationTime) {
        this.policyLastModificationTime = policyLastModificationTime;
    }

    public void setPolicyLastModificationTime(String policyLastModificationTimeString) {
        Date policyLastModificationTime = null;

        if (policyLastModificationTimeString != null) {
            try {
                policyLastModificationTime = df.parse(policyLastModificationTimeString);
            } catch (ParseException e) {
                log.error(String.format("Invalid date format for PAP: papAlias=\"%s\" papId=\"%s\"", alias, papId), e);
            }
        }

        this.policyLastModificationTime = policyLastModificationTime;
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

    public void setType(PSType pstype) {
        this.pstype = pstype;
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
        String typeString = String.format("%stype=%s\n", paddingString, pstype);
        String visibilityString = String.format("%svisibility=%s\n", paddingString, visibility);

        String dnString = String.format("%sdn=%s\n", paddingString, dn);
        String endpointString = String.format("%sendpoint=%s\n", paddingString, getEndpoint());

        if (pstype == PSType.LOCAL) {
            
            if (dn == null) {
                dnString = "";
            }
            
            if ((hostname == null) && (port == null)) {
                endpointString = "";
            }
        }

        return aliasString + typeString + visibilityString + dnString + endpointString;
    }

    public String toString() {
        String visibility = "visibility=";

        if (visibilityPublic) {
            visibility += "public";
        } else {
            visibility += "private";
        }
        return String.format("alias=\"%s\" type=\"%s\" visibility=\"%s\" dn=\"%s\" endpoint=\"%s\" id=\"%s\"",
                             alias,
                             pstype,
                             visibility,
                             dn,
                             getEndpoint(),
                             papId);
    }
}
