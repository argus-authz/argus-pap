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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.glite.authz.pap.common.xacml.wizard.XACMLWizard;
import org.glite.authz.pap.encoder.parser.BWParser;
import org.glite.authz.pap.encoder.parser.ParseException;

/**
 * This class is the public interface to parser of the simplified policy files.
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
     * 
     * @param stream
     *            The {@code InputStream} object from which to read the policy.
     */
    private void init(final InputStream stream) {
	if (parser != null) {
	    parser.ReInit(stream);
	} else {
	    parser = new BWParser(stream);
	}
    }

    /**
     * This function parses the input and returns a list of
     * {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard} objects
     * containing the parsed policies.
     * 
     * @return a {@code List}
     *         {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard}
     *         objects containing the parsed policies.
     * @throws EncodingException
     *             when there are problems during parsing
     */
    private List<XACMLWizard> doParse() throws EncodingException {
	try {
	    return parser.Text();
	} catch (ParseException e) {
	    throw new EncodingException(e);
	}
    }

    /**
     * This function parses the input and returns a list of
     * {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard} objects
     * containing the parsed policies.
     *
     * This function may be called multiple times, with different inputs, and
     * each time will parse the new input and provide a new set of parsed
     * policies.
     *
     * @param stream
     *            The {@code InputStream} object to parse.
     * @return a {@code List}
     *         {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard}
     *         objects containing the parsed policies.
     * @throws EncodingException
     *             when there are problems during parsing
     */
    public List<XACMLWizard> parse(final InputStream stream) throws EncodingException {
	init(stream);
	return doParse();
    }

    /**
     * This function parses the input and returns a list of
     * {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard} objects
     * containing the parsed policies.
     *
     * This function may be called multiple times, with different inputs, and
     * each time will parse the new input and provide a new set of parsed
     * policies.
     *
     * @param text
     *            The {@code String} object containing the policies to parse.
     * @return a {@code List}
     *         {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard}
     *         objects containing the parsed policies.
     * @throws EncodingException
     *             when there are problems during parsing
     */
    public List<XACMLWizard> parse(final String text) throws EncodingException {
	init(new ByteArrayInputStream(text.getBytes()));
	return doParse();
    }

    /**
     * This function parses the input and returns a list of
     * {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard} objects
     * containing the parsed policies.
     *
     * This function may be called multiple times, with different inputs, and
     * each time will parse the new input and provide a new set of parsed
     * policies.
     *
     * @param file
     *            The {@code File} object containing the policies to parse.
     * @return a {@code List}
     *         {@link org.glite.authz.pap.common.xacml.wizard.XACMLWizard}
     *         objects containing the parsed policies.
     * @throws EncodingException
     *             when there are problems during parsing
     */
    public List<XACMLWizard> parse(final File file) throws EncodingException {
	try {
	    init(new FileInputStream(file));
	} catch (FileNotFoundException e) {
	    throw new EncodingException(e);
	}
	return doParse();
    }

}
