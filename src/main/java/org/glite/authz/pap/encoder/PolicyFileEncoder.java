/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.glite.authz.pap.encoder.parser.BWParser;
import org.glite.authz.pap.encoder.parser.ParseException;

/**
 * This class is the public interface to parser of the simplified
 * policy files.
 *
 * @author Vincenzo Ciaschini
 */
public class PolicyFileEncoder {
    BWParser parser; // The effective unerlying parser

    /**
     * Basic constructor
     */
    public PolicyFileEncoder() {
        parser = null;
    }

    /**
     * Initializes the parser to read from a specified stream.
     * @param stream The {@code InputStream} object from which to read the policy.
     */
    private void init(InputStream stream) {
        if (parser != null) { 
            parser.ReInit(stream);
        }
        else {
            parser = new BWParser(stream);
        }
    }

    /**
     * This function parses the input and returns a list of 
     * {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard}
     * objects containing the parsed policies.
     * @return a {@code List} {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard} objects containing the parsed policies.
     * @throws EncodingException when there are problems during parsing
     */
    private List<XACMLWizard> doParse() throws EncodingException {
        try {
            return parser.Text();
        }
        catch (ParseException e) {
            throw new EncodingException(e);
        }
    }

    /**
     * This function parses the input and returns a list of 
     * {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard}
     * objects containing the parsed policies.
     *
     * This function may be called multiple times, with different inputs, and each time
     * will parse the new input and provide a new set of parsed policies.
     *
     * @param stream The {@code InputStream} object to parse.
     * @return a {@code List} {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard} objects containing the parsed policies.
     * @throws EncodingException when there are problems during parsing
     */
    public  List<XACMLWizard> parse(InputStream stream) throws EncodingException {
        init(stream);
        return doParse();
    }

    /**
     * This function parses the input and returns a list of 
     * {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard}
     * objects containing the parsed policies.
     *
     * This function may be called multiple times, with different inputs, and each time
     * will parse the new input and provide a new set of parsed policies.
     *
     * @param text The {@code String} object containing the policies to parse.
     * @return a {@code List} {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard} objects containing the parsed policies.
     * @throws EncodingException when there are problems during parsing
     */
    public  List<XACMLWizard> parse(String text) throws EncodingException {
        init(new ByteArrayInputStream(text.getBytes()));
        return doParse();
    }

    /**
     * This function parses the input and returns a list of 
     * {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard}
     * objects containing the parsed policies.
     *
     * This function may be called multiple times, with different inputs, and each time
     * will parse the new input and provide a new set of parsed policies.
     *
     * @param file The {@code File} object containing the policies to parse.
     * @return a {@code List} {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard} objects containing the parsed policies.
     * @throws EncodingException when there are problems during parsing
     */
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
