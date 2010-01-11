/*
 * Copyright 2009 Members of the EGEE Collaboration.
 * See http://www.eu-egee.org/partners for details on the copyright holders. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.server.standalone;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import org.mortbay.jetty.security.SslSelectChannelConnector;

/**
 * Any extension to the basic Jetty SSL connection handler that allows a pre-instantiated key and trust manager to be
 * used when create new SSL connections.
 */
public class JettySslSelectChannelConnector extends SslSelectChannelConnector {

    /** {@link KeyManager} used by this TLS connector. */
    private X509KeyManager keyManager;

    /** {@link TrustManager} used by this TLS connector. */
    private X509TrustManager trustManager;

    /**
     * Constructor.
     * 
     * @param key the key manager used for the TLS connections
     * @param trust the trust manager used for the TLS connections
     */
    public JettySslSelectChannelConnector(X509KeyManager key, X509TrustManager trust) {
        if(key == null){
            throw new IllegalArgumentException("X.509 key manager may not be null");
        }
        keyManager = key;
        
        if(trust == null){
            throw new IllegalArgumentException("X.509 trust manager may not be null");
        }
        trustManager = trust;
    }

    /** {@inheritDoc} */
    protected SSLContext createSSLContext() throws Exception {
        SSLContext sslConext = SSLContext.getInstance("TLS");
        sslConext.init(new KeyManager[] { keyManager }, new TrustManager[] { trustManager }, null);
        return sslConext;
    }
}