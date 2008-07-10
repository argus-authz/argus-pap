package org.glite.authz.pap.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.glite.authz.pap.authz.InitSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SecurityContextFilter implements Filter {

    protected Logger logger = LoggerFactory.getLogger( SecurityContextFilter.class );
    
    public void destroy() {

        // TODO Auto-generated method stub

    }

    public void doFilter( ServletRequest request, ServletResponse response,
            FilterChain chain ) throws IOException , ServletException {

        InitSecurityContext.setContextFromRequest( request );
        chain.doFilter( request, response );

    }

    public void init( FilterConfig filterConfig ) throws ServletException {

        // TODO Auto-generated method stub

    }

}
