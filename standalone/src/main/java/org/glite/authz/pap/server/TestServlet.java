package org.glite.authz.pap.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class TestServlet extends HttpServlet {

    public TestServlet() {

        
    }

    
    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException , IOException {
    
        resp.getWriter().println( "Hello World!" );
        return;
    }
}
