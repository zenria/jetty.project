/*******************************************************************************
 * Copyright (c) 1995-2012 Mort Bay Consulting Pty Ltd.
 * ======================================================================
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *   The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *
 *   The Apache License v2.0 is available at
 *   http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/
package org.eclipse.jetty.websocket;

import static org.hamcrest.Matchers.*;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.StdErrLog;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.helper.NoopWebSocket;
import org.eclipse.jetty.websocket.helper.WebSocketCaptureServlet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the extension chaining found to ensure that sequential extensions are called in the appropriate order.
 */
public class ExtensionChainingTest
{
    private Server server;
    private WebSocketCaptureServlet servlet;
    private URI serverUri;
    private WebSocketClientFactory clientFactory;

    @Before
    public void startServer() throws Exception
    {
        // Enable DEBUG logging for websocket classes.
        // enableDebugLogging(DebugExtension.class);

        // Configure Server
        server = new Server(0);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);

        // Serve capture servlet
        servlet = new WebSocketCaptureServlet();
        context.addServlet(new ServletHolder(servlet),"/");

        // Start Server
        server.start();

        Connector conn = server.getConnectors()[0];
        String host = conn.getHost();
        if (host == null)
        {
            host = "localhost";
        }
        int port = conn.getLocalPort();
        serverUri = new URI(String.format("ws://%s:%d/",host,port));
        // System.out.printf("Server URI: %s%n",serverUri);

        // Enable Debug Extension
        servlet.getWebSocketFactory().getExtensionManager().registerExtension("debug",DebugExtension.class);

        // Setup Client Factories
        clientFactory = new WebSocketClientFactory();
        clientFactory.start();
    }

    public static void enableDebugLogging(Class<?> clazz)
    {
        Logger log = Log.getLogger(clazz);
        if (log instanceof StdErrLog)
        {
            ((StdErrLog)log).setLevel(StdErrLog.LEVEL_DEBUG);
        }
    }

    @After
    public void stopServer() throws Exception
    {
        clientFactory.stop();

        server.stop();
    }

    @Test
    public void testExtensionChain() throws Exception
    {
        DebugExtension.setCapture("");

        WebSocketClient client = clientFactory.newWebSocketClient();
        client.setExtensions("debug; id=1", "debug; id=2", "debug; id=3", "debug; id=4");
        WebSocket.Connection conn = null;

        try
        {
            conn = client.open(serverUri,new NoopWebSocket(),2,TimeUnit.SECONDS);
            conn.sendMessage("hello world");

            Assert.assertThat("servlet",servlet.captures.size(),is(1));
            Assert.assertThat("capture text", servlet.captures.get(0).messages, contains("hello world"));
        }
        finally
        {
            close(conn);
        }

        clientFactory.stop();

        // syntax is (in|out) "[" (text|close|binary) (id param) "]"
        // such that "in[t3]" means inbound text message on debug #3.
        String expectedCapture = "in[t1]in[t2]in[t3]in[t4]in[c1]in[c2]in[c3]in[c4]out[c4]out[c3]out[c2]out[c1]";
        String actualCapture = DebugExtension.getCapture();
        Assert.assertThat("Order Capture",actualCapture,is(expectedCapture));
    }

    private void close(Connection conn)
    {
        if (conn == null)
        {
            return;
        }
        conn.close();
    }
}
