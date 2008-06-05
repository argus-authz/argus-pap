package org.glite.authz.pap.encoder;

import java.util.Vector;
import java.util.Enumeration;
import java.lang.String;

class SingleCondition {
    int type;
    String value1;
    String value2;
    boolean yesorno;
    public static final int TYPE_FQAN     = 0;
    public static final int TYPE_DN       = 1;
    public static final int TYPE_GA       = 2;
    public static final int TYPE_CERT     = 3;
    public static final int TYPE_PILOT    = 4;
    public static final int TYPE_RESOURCE = 5;
};

class ConditionRow {
    Vector singles;
    public ConditionRow() {
        singles = new Vector();
    }
};

class ConditionList {
    Vector rows;

    public ConditionList() {
        rows = new Vector();
    }
};

class FullCondition {
    boolean allow;
    boolean isprivate;
    ConditionRow simples;
    ConditionList excepts;

    public FullCondition() {
        simples = new ConditionRow();
        excepts = new ConditionList();
    }
};

class Conds {
    Vector fullconditions;

    public Conds() {
        fullconditions = new Vector();
    }
};

class Policy {
    String name;
    int type;
    Conds conds;
    Policies policies;
    public static final int POLICY_TYPE_BW    = 0;
    public static final int POLICY_TYPE_CLASS = 1;
    private int index;

    public Policy() {
        type = POLICY_TYPE_BW;
    }

    public String Output() {
        if (type == POLICY_TYPE_BW)
            return OutputBW();
        else
            return OutputSClass();
    }

    private String OutputBW() {
        String XACML="<xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<PolicySet\n" +
    "  xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\"\n" +
    "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    "  xsi:schemaLocation=\"urn:oasis:names:tc:xacml:schema:os http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-os.xsd\"\n" +
    "  PolicySetId=\"black-and-white\"" +
    "  PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:2.0:policy-combining-algorithm:ordered-deny-overrides\">" +
            " <Target></Target>\n";

        int i = 0;
        int j = 0;

        Enumeration conditions = conds.fullconditions.elements();

        while (conditions.hasMoreElements()) {
            FullCondition fc = (FullCondition)conditions.nextElement();
            XACML += OutputFC(fc);
        }
        XACML += "</PolicySet>";
        return XACML;
    }

    private String OutputSClass() {
        String XACML = "<xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<PolicySet\n" +
    "  xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\"\n" +
    "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    "  xsi:schemaLocation=\"urn:oasis:names:tc:xacml:schema:os http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-os.xsd\"\n" +
    "  PolicySetId=\"specificclass\"" +
    "  PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:2.0:policy-combining-algorithm:ordered-deny-overrides\">" +
    " <Target></Target>\n";
        
        XACML += policies.Output();

        XACML += 
    " <Obligations>\n" +
    "   <Obligation ObligationId=\"SERVICE_CLASS_ASSIGNMENT\" FulfillOn=\"Permit\">\n" +
    "     <AttributeAssignment AttributeId=\"SERVICECLASS\">\n       ";

        XACML += name;
        XACML += "\n" +
    "     </AttributeAssignment>\n" +
    "   </Obligation>\n" +
    " </Obligations>\n" +
    "</PolicySet>\n";
        return XACML;
    }

    private String OutputFC(FullCondition fc) {
        String XACML = "<Policy\n" +
      "  PolicyId=\"policy" + index++ + "\"\n" +
      "  RuleCombiningAlgorithmId=\"urn:oasis:names:tc:xacml:2.0:policy-combining-algorithm:ordered-deny-override\">\n" +
      "   <Target>\n" +
      "     <Resources>\n" +
      "       <Resource>\n" +
      "         " + name + "\n" +
      "       </Resource>\n" +
      "     </Resources>\n" +
      "   </Target>\n";

        ConditionRow denys = fc.simples;
        ConditionList excepts = fc.excepts;

        XACML += "   <Rule RuleId=\"rule" + index++ +"\n" +
            "         Effect=\"";
        if (fc.allow)
            XACML += "Permit";
        else
            XACML += "Deny";
                
        XACML += "\">\n" +
      "     <Condition>\n" +
      "       <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0;function:and\">\n";

        Enumeration singles = denys.singles.elements();
        while (singles.hasMoreElements()) {
            XACML += OutputCondition((SingleCondition) singles.nextElement());
        }

        if (excepts != null) {
            Enumeration exceptlist = excepts.rows.elements();
            while (exceptlist.hasMoreElements()) {
                XACML +=  "         <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:not\">\n" +
                    "           <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0;function:and\">\n";
                Enumeration singlesexcepts = ((ConditionRow)(exceptlist.nextElement())).singles.elements();
                while (singlesexcepts.hasMoreElements()) {
                    XACML += OutputCondition((SingleCondition) singlesexcepts.nextElement());
                }
                XACML +=       "           </Apply>\n" +
                    "         </Apply>\n";
            }
        }

        XACML +=       "       </Apply>\n" +
      "     </Condition>\n" +
      "   </Rule>\n" +
            " </Policy>\n";

        return XACML;
    }

    private String OutputCondition(SingleCondition sc) {
        String XACML = new String();

        switch (sc.type) {

        case SingleCondition.TYPE_FQAN:
            XACML = 
                    "       <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:any-of\">\n" +
                    "         <Function FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                    "         <AttributeValue DataType=\"FQAN\">" + sc.value1 + "</AttributeValue>\n" +
                    "         <SubjectAttributeDesignator \n" +
                    "           AttributeId=\"FQANS\" DataType=\"FQAN\">\n" +
                    "       </Apply>\n";
            break;
            
        case SingleCondition.TYPE_DN:
            XACML =
                    "       <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:any-of\">\n" +
                    "         <Function FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                    "         <AttributeValue DataType=\"DN\">" + sc.value1 + "</AttributeValue>\n" +
                    "         <SubjectAttributeDesignator \n" +
                    "           AttributeId=\"FQANS\" DataType=\"FQAN\">\n" +
                    "       </Apply>\n";
            break;

        case SingleCondition.TYPE_GA:
            XACML =
                    "       <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:any-of\">\n" +
                    "         <Function FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                    "         <AttributeValue DataType=\"GA\">" +
                sc.value1 + "=" + sc.value2 + "</AttributeValue>\n" +
                    "         <SubjectAttributeDesignator \n" +
                    "           AttributeId=\"GenericAttribute\" DataType=\"GA\">\n" +
                "       </Apply>\n";
            break;

        case SingleCondition.TYPE_CERT:
            XACML =
                    "       <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:any-of\">\n" +
                    "         <Function FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                    "         <AttributeValue DataType=\"CERT\">" +
                sc.value1 + ":" + sc.value2 +"</AttributeValue>\n" +
                    "         <SubjectAttributeDesignator \n" +
                    "           AttributeId=\"CertificateIdentifier\" DataType=\"CERT\">\n" +
                "       </Apply>\n";
            break;

        case SingleCondition.TYPE_PILOT:
            XACML =
                    "       <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:any-of\">\n" +
                    "         <Function FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
                    "         <AttributeValue DataType=\"PILOT\">" +
                (sc.yesorno ? "Yes" : "No") +"</AttributeValue>\n" +
                    "         <SubjectAttributeDesignator \n" +
                "           AttributeId=\"PilotIdentifier\" DataType=\"PILOT\">\n"; 
            break;
        }
        return XACML;
    }
};

class Policies {
    Vector policies;

    public Policies() {
        policies = new Vector();
    }

    public String Output() {
        String XACML = new String();
        Enumeration i = policies.elements();

        while (i.hasMoreElements()) {
            XACML += ((Policy)i.nextElement()).Output();
        }
        return XACML;
    }
};

