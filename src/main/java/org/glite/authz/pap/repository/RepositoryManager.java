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

package org.glite.authz.pap.repository;

import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemRepositoryManager;
import org.glite.authz.pap.repository.exceptions.InvalidVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an Abstract class used to bootstrap and initialize the repository. This class must be extended by the
 * specific repository implementation class.
 * <p>
 * 
 * @see FileSystemRepositoryManager
 * @see DAOFactory
 */
public abstract class RepositoryManager {

    public static final String REPOSITORY_MANAGER_VERSION = "1";

    private static final Logger log = LoggerFactory.getLogger(RepositoryManager.class);
    private static boolean initialized = false;

    protected RepositoryManager() {}

    /**
     * Call the bootstrap() method before using this class.
     */
    public static void bootstrap() {

        FileSystemRepositoryManager.getInstance().initialize();

        checkVersion();

        initialized = true;
    }

    /**
     * Checks that the version of an already existing repository is supported by this manager.
     * 
     * @throws InvalidVersionException if the version of the existing repository is not supported.
     */
    private static void checkVersion() {
        String repositoryVersion = FileSystemRepositoryManager.getInstance().getRepositoryVersion();

        if (!(REPOSITORY_MANAGER_VERSION.equals(repositoryVersion))) {
            throw new InvalidVersionException("Invalid repository version (v" + repositoryVersion
                    + "). Requested version is v" + REPOSITORY_MANAGER_VERSION);
        }

        log.info("Repository version: v" + repositoryVersion);
    }
     

    /**
     * Returns the version of the existing repository.
     * 
     * @return the version of the repository as <code>String</code>. The version is an integer
     *         number, higher that number is and more recent is the version of the repository.
     */
    protected abstract String getRepositoryVersion();

    /**
     * Initialize the repository. These method is called by the <code>bootstrap()</code> method of
     * {@link RepositoryManager} before calling any other method of the repository manager class
     * implementation.
     */
    protected abstract void initialize();
}
