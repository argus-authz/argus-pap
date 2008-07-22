package org.glite.authz.pap.encoder;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.lang.String;
import org.glite.authz.pap.ui.wizard.*;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import java.util.List;
import org.opensaml.xacml.XACMLObject;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.apache.log4j.PropertyConfigurator;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.XMLConfigurator;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;

public class PolicyFileEncoder {
    BWParser parser;

    public PolicyFileEncoder() {
        parser = null;
    }

    private void init(InputStream stream) {
        if (parser != null) { 
            parser.ReInit(stream);
        }
        else {
            parser = new BWParser(stream);
        }
    }

    private List<XACMLObject> doParse() throws EncodingException {
        try {
            return parser.Text();
        }
        catch (ParseException e) {
            throw new EncodingException(e);
        }
    }

    public  List<XACMLObject> parse(InputStream stream) throws EncodingException {
        init(stream);
        return doParse();
    }

    public  List<XACMLObject> parse(String text) throws EncodingException {
        init(new ByteArrayInputStream(text.getBytes()));
        return doParse();
    }

    public  List<XACMLObject> parse(File file) throws EncodingException {
        try {
            init(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new EncodingException(e);
        }
        return doParse();
    }

    private static void print(PolicySetType set) {
        System.out.println("-----------------");
        System.out.println("SET: " + PolicySetHelper.getInstance().toString(set));
        
    }

    private static void print(PolicyType policy) {
        System.out.println("-----------------");
        System.out.println("POLICY: " + PolicyHelper.getInstance().toString(policy));
    }

    public static void main(String[] args) {
        try{
            DefaultBootstrap.bootstrap();
            XMLConfigurator xmlConfigurator = new XMLConfigurator();
            xmlConfigurator.load( Configuration.class.getResourceAsStream( "/opensaml_bugfix.xml" ) ); 
        }
        catch (ConfigurationException e) {
            System.out.println(e.toString());
            return;
        }
        PolicyFileEncoder encoder = new PolicyFileEncoder();

        try {
            if (args.length > 0) {
                int i = 0;
                while (i < args.length) {
                    File f = new File(args[i++]);
                    List<XACMLObject> list = encoder.parse(f);

                    for (XACMLObject xacml: list) {
                        System.out.println(xacml.getClass().getName());
                        if (xacml instanceof org.opensaml.xacml.policy.PolicySetType) {
                            print((org.opensaml.xacml.policy.PolicySetType)xacml);
                        }
                        else if (xacml instanceof org.opensaml.xacml.policy.PolicyType) {
                            print((org.opensaml.xacml.policy.PolicyType)xacml);
                        }
                    }
                }
            }
            else
                System.out.println(encoder.parse(System.in));
        }
        catch (EncodingException e) {
            System.out.println(e.toString());
        }
    }
}
