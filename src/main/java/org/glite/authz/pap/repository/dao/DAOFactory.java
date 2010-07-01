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

package org.glite.authz.pap.repository.dao;

import org.glite.authz.pap.repository.dao.filesystem.FileSystemDAOFactory;

/**
 * This class conforms to the Abstract Factory pattern to produce a number of DAOs needed by the
 * application.
 */
public abstract class DAOFactory {

    /**
     * Returns the DAO factory implementation.
     * 
     * @return the DAO factory implementation class.
     */
    public static DAOFactory getDAOFactory() {
        return FileSystemDAOFactory.getInstance();
    }

    /**
     * Returns the PolicySet DAO.
     * 
     * @return the PolicySet DAO implementaion class.
     */
    public abstract PolicySetDAO getPolicySetDAO();

    /**
     * Returns the Policy DAO.
     * 
     * @return the Policy DAO implementaion class.
     */
    public abstract PolicyDAO getPolicyDAO();

    /**
     * Returns the Pap DAO.
     * 
     * @return the Pap DAO implementaion class.
     */
    public abstract PapDAO getPapDAO();
}
