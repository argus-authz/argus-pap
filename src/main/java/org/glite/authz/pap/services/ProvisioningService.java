/**
 * 
 * Copyright 2006-2007 Istituto Nazionale di Fisica Nucleare (INFN)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * File : ProvisioningService.java
 * 
 * Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>
 * 
 */

package org.glite.authz.pap.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.glite.authz.pap.authz.operations.policyprovisioning.GetPoliciesForPAPOperation;
import org.glite.authz.pap.authz.operations.policyprovisioning.GetPoliciesForPDPOperation;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
import org.glite.authz.pap.repository.PersistenceManager;
import org.glite.authz.pap.services.exceptions.MissingIssuerException;
import org.glite.authz.pap.services.exceptions.VersionMismatchException;
import org.glite.authz.pap.services.exceptions.WrongFormatIssuerException;
import org.glite.authz.pap.services.provisioning.axis_skeletons.Provisioning;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.core.Response;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisioningService implements Provisioning {

    private final Logger log = LoggerFactory.getLogger(ProvisioningService.class);
    private static final Object lock = new Object();

    public Response XACMLPolicyQuery(XACMLPolicyQueryType query) throws java.rmi.RemoteException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) MessageContext.getCurrentContext()
                                                                                   .getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);

        // lock need to keep memory usage low, it's not possible to re-use opensaml objects so they have to
        // be cloned for every request.
        synchronized (lock) {

            try {

                PersistenceManager.getInstance().getCurrentSession().beginTransaction();

                // log the received query
                log.trace("Received XACLMPolicyQuery " + XMLObjectHelper.toString(query));

                /* check a few things about the query */
                try {

                    ServicesUtils.checkQuery(query);

                } catch (VersionMismatchException e) {

                    ServicesExceptionManager.logAndRollback(log, e);
                    return ServicesUtils.createResponse(query, e);

                } catch (MissingIssuerException e) {

                    ServicesExceptionManager.logAndRollback(log, e);
                    return ServicesUtils.createResponse(query, e);

                } catch (WrongFormatIssuerException e) {

                    ServicesExceptionManager.logAndRollback(log, e);
                    return ServicesUtils.createResponse(query, e);

                }

                /* get local policies */

                List<XACMLObject> resultList = null;

                /*
                 * TODO discrimination between a PAP and a PDP is done after the presence of the Extensions element, too
                 * simplistic
                 */

                Extensions extensions = query.getExtensions();

                if (extensions == null)

                    resultList = GetPoliciesForPDPOperation.instance().execute();

                else

                    resultList = GetPoliciesForPAPOperation.instance().execute();

                /* prepare the response */

                Response response = ServicesUtils.createResponse(query, resultList, httpServletRequest);

                PersistenceManager.getInstance().getCurrentSession().getTransaction().commit();

                log.trace("Sending Response : " + XMLObjectHelper.toString(query));

                return response;

            } catch (RuntimeException e) {
                ServicesExceptionManager.logAndRollback(log, e);
                throw e;
            }
        }
    }
}
