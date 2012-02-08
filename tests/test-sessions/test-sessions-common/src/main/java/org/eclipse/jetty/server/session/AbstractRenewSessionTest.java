package org.eclipse.jetty.server.session;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.security.Realm;
import org.eclipse.jetty.client.security.SimpleRealmResolver;

import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.security.Constraint;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;



/**
 * AbstractRenewSessionTest
 *
 * Test the ability to change an existing session id after authentication has 
 * happened. Test also this ability in conjunction with cross context dispatch.
 */
public abstract class AbstractRenewSessionTest
{ 
    public abstract AbstractTestServer createServer(int port, int max, int scavenge);

    public void pause(int scavenge)
    {
        try
        {
            Thread.sleep(scavenge * 2500L);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    public String extractSessionId (String cookieStr)
    {
        if (cookieStr == null)
            return null;
        if ("".equals(cookieStr))
            return "";
        
        int i = cookieStr.indexOf("JSESSIONID=");
        if (i < 0)
            return null;
        return (cookieStr.substring(i+11, cookieStr.indexOf(';', i)));
    }

    @Test
    public void testRenewedSession() throws Exception
    {
        String contextPathA = "/a";
        String contextPathB = "/b";
            
        String servletMapping = "/server";
        String authMapping = "/auth";
        int scavengePeriod = 3;
        AbstractTestServer server = createServer(0, 1, scavengePeriod);
        ServletContextHandler contextA = server.addContext(contextPathA);
        contextA.addServlet(TestServletA.class, servletMapping);
        contextA.addServlet(TestServletA.class, authMapping);
        
        
        //create a security constraint that will require authentication and put it on contextA
        File realmPropFile = MavenTestingUtils.getTestResourceFile("realm.properties");
        LoginService loginService = new HashLoginService("MyRealm",realmPropFile.getAbsolutePath());
        server.getServer().addBean(loginService); 

        ConstraintSecurityHandler security = new ConstraintSecurityHandler();
        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate( true );
        constraint.setRoles(new String[]{"user", "admin"});

        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec( authMapping );
        mapping.setConstraint( constraint );

        Set<String> knownRoles = new HashSet<String>();
        knownRoles.add("user");
        knownRoles.add("admin");
        
        security.setConstraintMappings(Collections.singletonList(mapping), knownRoles);
        security.setAuthenticator(new BasicAuthenticator());
        security.setLoginService(loginService);
        security.setStrict(false);
        contextA.setSecurityHandler(security);
        
        //create a second context to test cross context dispatch
        ServletContextHandler contextB = server.addContext(contextPathB);
        contextB.addServlet(TestServletB.class, servletMapping);
        
        
        server.start();
        int port=server.getPort();
        try
        {
            HttpClient client = new HttpClient();
            client.setConnectorType(HttpClient.CONNECTOR_SOCKET);
            client.setRealmResolver(new SimpleRealmResolver(new Realm()
            {
                public String getId()
                {
                    return "MyRealm";
                }
           
                public String getPrincipal()
                {
                    return "jetty";
                }
           
                public String getCredentials()
                {
                    return "jetty";
                }
            }));
            
            
            client.start();
            try
            {
                //Hit a servlet on contextA that creates a session
                ContentExchange exchange = new ContentExchange(true);
                exchange.setMethod(HttpMethods.GET);
                exchange.setURL("http://localhost:" + port + contextPathA + servletMapping + "?action=create");
                client.send(exchange);
                exchange.waitForDone();
                assertEquals(HttpServletResponse.SC_OK,exchange.getResponseStatus());
                String sessionCookieA = exchange.getResponseFields().getStringField("Set-Cookie");
                assertTrue(sessionCookieA != null);
                // Mangle the cookie, replacing Path with $Path, etc.
                sessionCookieA = sessionCookieA.replaceFirst("(\\W)(P|p)ath=", "$1\\$Path=");
                String sessionIdA1 = extractSessionId(sessionCookieA);    
               
                
                //Hit contextA with a forward to context B to cause a session with the same Id there
                exchange = new ContentExchange(true);
                exchange.setMethod(HttpMethods.GET);
                exchange.setURL("http://localhost:" + port + contextPathA + servletMapping);
                exchange.getRequestFields().add("Cookie", sessionCookieA); 
                client.send(exchange);
                exchange.waitForDone();
                String sessionCookieB = exchange.getResponseFields().getStringField("Set-Cookie");
                assertTrue(sessionCookieB != null);
                // Mangle the cookie, replacing Path with $Path, etc.
                sessionCookieB = sessionCookieB.replaceFirst("(\\W)(P|p)ath=", "$1\\$Path=");
                String sessionIdB1 = extractSessionId(sessionCookieB);
                

                //Authenticate to contextA, causing the sessionId to be changed on it
                exchange = new ContentExchange(true);
                exchange.setMethod(HttpMethods.GET);
                exchange.setURL("http://localhost:" + port + contextPathA + authMapping);
                exchange.getRequestFields().add("Cookie", sessionCookieA);  
                client.send(exchange);
                exchange.waitForDone();
                assertEquals(HttpServletResponse.SC_OK,exchange.getResponseStatus());                
                //Check that sessionid has changed on 1st context
                sessionCookieA = exchange.getResponseFields().getStringField("Set-Cookie");
                // Mangle the cookie, replacing Path with $Path, etc.
                sessionCookieA = sessionCookieA.replaceFirst("(\\W)(P|p)ath=", "$1\\$Path=");
                assertTrue(sessionCookieA != null);
                String sessionIdA2 = extractSessionId(sessionCookieA);            
                assertTrue(!sessionIdA2.equals(sessionIdA1));
             
                
                //Now hit contextB again and check that its session is not new and matches the changed session id
                sessionCookieB = sessionCookieB.replaceFirst(sessionIdB1,sessionIdA2);
                exchange = new ContentExchange(true);
                exchange.setMethod(HttpMethods.GET);
                exchange.setURL("http://localhost:" + port + contextPathB + servletMapping);
                exchange.getRequestFields().add("Cookie", sessionCookieB); 
                client.send(exchange);
                exchange.waitForDone();
            }
            finally
            {
                client.stop();
            }
        }
        finally
        {
            server.stop();
        }

    }
    
    
    public static class TestServletA extends HttpServlet
    {
        public String createId;
        public String authId;
        public int createdHashCode;
        
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            String action = request.getParameter("action");
            if ("create".equals(action))
            {
                HttpSession session = request.getSession(true);
                createId = session.getId();
                authId = null;
                createdHashCode = session.hashCode();
            }
            else if (request.getServletPath().contains("auth"))
            {
                HttpSession session = request.getSession(false);
                assertTrue(session != null); //if here, got authenticated
                authId = session.getId();
                assertTrue(createId != null);
                assertTrue(!createId.equals(authId));
                assertEquals(createdHashCode, session.hashCode()); //prove its the same session
            }
            else
            {
                //forward to the second context
                ServletContext contextB = getServletContext().getContext("/b");
                RequestDispatcher dispatcherB = contextB.getRequestDispatcher(request.getServletPath()+"?action=create");
                dispatcherB.forward(request, response);
            }
        }
    }
    
    
    public static class TestServletB extends HttpServlet
    {
        public int createdHashCode;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            String action = request.getParameter("action");
            if ("create".equals(action))
            {
                HttpSession session = request.getSession(true);
                assertTrue(session.isNew());
                createdHashCode = session.hashCode();
            }
            else
            {
                HttpSession session = request.getSession(false);
                assertTrue(session != null);
                assertTrue(!session.isNew());
                assertEquals(createdHashCode, session.hashCode());
            }
        }
    }
}
