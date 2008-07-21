/**************************************************************************

 Copyright 2006-2007 Istituto Nazionale di Fisica Nucleare (INFN)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 File : PAPConfiguration.java

 Authors: Andrea Ceccanti <andrea.ceccanti@cnaf.infn.it>
          Valerio Venturi <valerio.venturi@cnaf.infn.it>

 **************************************************************************/

package org.glite.authz.pap.common;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.INIConfiguration;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPConfiguration {

    private static final String DEFAULT_PAP_CONFIGURATION_DIR = "/opt/glite/etc/pap";

    private static final String DEFAULT_PAP_REPOSITORY_DIR = "/var/glite/etc/pap";

    private static final String DEFAULT_PAP_DISTRIBUTION_FILE_NAME = "pap_distribution.ini";

    private static final String DEFAULT_PAP_AUTHZ_FILE_NAME = "pap_authorization.ini";

    final static Logger logger = LoggerFactory
	    .getLogger(PAPConfiguration.class);

    private static PAPConfiguration instance;

    private CompositeConfiguration configuration;

    private PAPConfiguration(String papConfigurationDir, String papRepositoryDir) {

	configuration = new CompositeConfiguration();

	setupConfigurationDirectories(papConfigurationDir, papRepositoryDir);

	loadPolicyDistributionConfiguration();

    }

    private void setupConfigurationDirectories(String papConfDir,
	    String papRepoDir) {

	// String papConfDir = context.getInitParameter( "papConfigurationDir"
	// );
	// String papRepoDir = context.getInitParameter( "papRepositoryDir" );

	if (papConfDir == null)
	    papConfDir = DEFAULT_PAP_CONFIGURATION_DIR;
	if (papRepoDir == null)
	    papRepoDir = DEFAULT_PAP_REPOSITORY_DIR;

	configuration.setProperty("papConfigurationDir", papConfDir);
	configuration.setProperty("papRepositoryDir", papRepoDir);
    }

    public static PAPConfiguration instance() {

	if (instance == null)
	    throw new PAPConfigurationException(
		    "Please initialize configuration before calling the instance method!");

	return instance;

    }

    public static PAPConfiguration initialize(ServletContext context) {

	if (instance == null) {
	    String papConfDir = context.getInitParameter("papConfigurationDir");
	    String papRepoDir = context.getInitParameter("papRepositoryDir");

	    return initialize(papConfDir, papRepoDir);
	}

	return instance;
    }

    public static PAPConfiguration initialize() {
	if (instance == null)
	    return initialize(null, null);

	return instance;

    }

    public static PAPConfiguration initialize(String papConfigurationDir,
	    String papRepositoryDir) {

	if (instance == null)
	    instance = new PAPConfiguration(papConfigurationDir,
		    papRepositoryDir);

	return instance;
    }

    private void loadPolicyDistributionConfiguration() {

	logger.info("Loading policy distribution configuration...");
	String papConfDir = configuration.getString("papConfigurationDir");

	File papDistConfFile = new File(papConfDir + "/"
		+ DEFAULT_PAP_DISTRIBUTION_FILE_NAME);

	if (!papDistConfFile.exists())
	    throw new PAPConfigurationException(
		    "PAP distribution configuration file does not exists on path:"
			    + papDistConfFile.getAbsolutePath());

	try {

	    configuration
		    .addConfiguration(new INIConfiguration(papDistConfFile));

	} catch (org.apache.commons.configuration.ConfigurationException e) {

	    logger.error("Error parsing PAP distribution configuration: "
		    + e.getMessage(), e);
	    throw new PAPConfigurationException(
		    "Error parsing PAP distribution configuration: "
			    + e.getMessage(), e);

	}

    }

    // Getter methods that access the configuration object and other services
    // down here

    public String getPAPConfigurationDir() {
	return configuration.getString("papConfigurationDir");

    }

    public String getPAPRepositoryDir() {
	return configuration.getString("papRepositoryDir");
    }

    public String getPapAuthzConfigurationFileName() {

	return getPAPConfigurationDir() + "/" + DEFAULT_PAP_AUTHZ_FILE_NAME;
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {

	return configuration.getBigDecimal(key, defaultValue);
    }

    public BigDecimal getBigDecimal(String key) {

	return configuration.getBigDecimal(key);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {

	return configuration.getBigInteger(key, defaultValue);
    }

    public BigInteger getBigInteger(String key) {

	return configuration.getBigInteger(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {

	return configuration.getBoolean(key, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {

	return configuration.getBoolean(key, defaultValue);
    }

    public boolean getBoolean(String key) {

	return configuration.getBoolean(key);
    }

    public byte getByte(String key, byte defaultValue) {

	return configuration.getByte(key, defaultValue);
    }

    public Byte getByte(String key, Byte defaultValue) {

	return configuration.getByte(key, defaultValue);
    }

    public byte getByte(String key) {

	return configuration.getByte(key);
    }

    public double getDouble(String key, double defaultValue) {

	return configuration.getDouble(key, defaultValue);
    }

    public Double getDouble(String key, Double defaultValue) {

	return configuration.getDouble(key, defaultValue);
    }

    public double getDouble(String key) {

	return configuration.getDouble(key);
    }

    public float getFloat(String key, float defaultValue) {

	return configuration.getFloat(key, defaultValue);
    }

    public Float getFloat(String key, Float defaultValue) {

	return configuration.getFloat(key, defaultValue);
    }

    public float getFloat(String key) {

	return configuration.getFloat(key);
    }

    public int getInt(String key, int defaultValue) {

	return configuration.getInt(key, defaultValue);
    }

    public int getInt(String key) {

	return configuration.getInt(key);
    }

    public Integer getInteger(String key, Integer defaultValue) {

	return configuration.getInteger(key, defaultValue);
    }

    public Iterator getKeys() {

	return configuration.getKeys();
    }

    public Iterator getKeys(String key) {

	return configuration.getKeys(key);
    }

    public List getList(String key, List defaultValue) {

	return configuration.getList(key, defaultValue);
    }

    public List getList(String key) {

	return configuration.getList(key);
    }

    public long getLong(String key, long defaultValue) {

	return configuration.getLong(key, defaultValue);
    }

    public Long getLong(String key, Long defaultValue) {

	return configuration.getLong(key, defaultValue);
    }

    public long getLong(String key) {

	return configuration.getLong(key);
    }

    public Properties getProperties(String key, Properties defaults) {

	return configuration.getProperties(key, defaults);
    }

    public Properties getProperties(String key) {

	return configuration.getProperties(key);
    }

    public Object getProperty(String key) {

	return configuration.getProperty(key);
    }

    public short getShort(String key, short defaultValue) {

	return configuration.getShort(key, defaultValue);
    }

    public Short getShort(String key, Short defaultValue) {

	return configuration.getShort(key, defaultValue);
    }

    public short getShort(String key) {

	return configuration.getShort(key);
    }

    public String getString(String key, String defaultValue) {

	return configuration.getString(key, defaultValue);
    }

    public String getString(String key) {

	return configuration.getString(key);
    }

    public String[] getStringArray(String key) {

	return configuration.getStringArray(key);
    }

}
