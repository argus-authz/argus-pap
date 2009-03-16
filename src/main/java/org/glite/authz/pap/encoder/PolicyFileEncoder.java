package org.glite.authz.pap.encoder;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.lang.String;

import org.glite.authz.pap.common.xacml.wizard.*;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import java.util.List;
import org.opensaml.xacml.XACMLObject;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
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

    private List<XACMLWizard> doParse() throws EncodingException {
        try {
            return parser.Text();
        }
        catch (ParseException e) {
            throw new EncodingException(e);
        }
    }

    public  List<XACMLWizard> parse(InputStream stream) throws EncodingException {
        init(stream);
        return doParse();
    }

    public  List<XACMLWizard> parse(String text) throws EncodingException {
        init(new ByteArrayInputStream(text.getBytes()));
        return doParse();
    }

    public  List<XACMLWizard> parse(File file) throws EncodingException {
        try {
            init(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new EncodingException(e);
        }
        return doParse();
    }

//      private static void print(XACMLWizard setw) {
//          System.out.println(setw.toFormattedString(false));
//      }

//       public static void main(String[] args) {
//           try{
//               DefaultBootstrap.bootstrap();
//               //            XMLConfigurator xmlConfigurator = new XMLConfigurator();
//               //            xmlConfigurator.load( Configuration.class.getResourceAsStream( "/opensaml_bugfix.xml" ) ); 
//           }
//           catch (ConfigurationException e) {
//               System.out.println(e.toString());
//               return;
//           }
//           PolicyFileEncoder encoder = new PolicyFileEncoder();

//           try {
//               if (args.length > 0) {
//                   int i = 0;
//                   while (i < args.length) {
//                       System.out.println("Try " + args[i]);
//                       File f = new File(args[i++]);
//                       List<XACMLWizard> list = encoder.parse(f);

//                       for (XACMLWizard xacml: list) {
//                           System.out.println(xacml.getClass().getName());
//                           print(xacml);
//                       }
//                   }
//               }
//               else
//                   System.out.println(encoder.parse(System.in));
//           }
//           catch (EncodingException e) {
//               System.out.println(e.toString());
//           }
//       }
}
