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

package org.glite.authz.pap.repository.dao.filesystem;

import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PapDAO;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;

public class FileSystemDAOFactory extends DAOFactory {
    private static FileSystemDAOFactory instance = null;

    public static FileSystemDAOFactory getInstance() {
        if (instance == null) {
            instance = new FileSystemDAOFactory();
        }
        return instance;
    }

    private FileSystemDAOFactory() {}

    @Override
    public PolicyDAO getPolicyDAO() {
        return FileSystemPolicyDAO.getInstance();
    }

    @Override
    public PolicySetDAO getPolicySetDAO() {
        return FileSystemPolicySetDAO.getInstance();
    }

    @Override
    public PapDAO getPapDAO() {
        return FileSystemPapDAO.getInstance();
    }

}
