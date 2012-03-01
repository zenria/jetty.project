package org.eclipse.jetty.websocket.mux;

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
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.eclipse.jetty.websocket.helper.NoopWebSocket;
import org.eclipse.jetty.websocket.helper.WebSocketCaptureServlet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MuxExtensionTest
{
    private Server server;
    private WebSocketCaptureServlet servlet;
    private URI serverUri;
    private WebSocketClientFactory clientFactory;

    @Before
    public void startServer() throws Exception
    {
        // Enable DEBUG logging for websocket classes.
        enableDebugLogging(MuxExtension.class);
        enableDebugLogging(MuxClientExtension.class);
        enableDebugLogging(MuxServerExtension.class);
        enableDebugLogging(MuxChannel.class);
        enableDebugLogging(MuxLogicalConnection.class);
        enableDebugLogging(MuxedWebSocket.class);
        enableDebugLogging(MuxSubChannel.class);
        enableDebugLogging(WebSocketClient.class);
        enableDebugLogging(WebSocketClientFactory.class);

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

        clientFactory = new WebSocketClientFactory();
        clientFactory.start();
    }

    private void enableDebugLogging(Class<?> clazz)
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
    public void testSingleConnection() throws Exception
    {
        WebSocketClient client = clientFactory.newWebSocketClient();

        client.setExtensions("mux");
        WebSocket.Connection conn = null;

        try
        {
            conn = client.open(serverUri,new NoopWebSocket(),2,TimeUnit.SECONDS);
            conn.sendMessage("hello world");

            Assert.assertThat("servlet",servlet.captures.size(),is(1));
        }
        finally
        {
            close(conn);
        }
    }

    @Test
    public void testTwoConnections() throws Exception
    {
        WebSocketClient client = clientFactory.newWebSocketClient();

        client.setExtensions("mux");
        WebSocket.Connection conn1 = null;
        WebSocket.Connection conn2 = null;

        try
        {
            // Mimicing the Example found in the MUX spec 03 (section 8)
            // http://tools.ietf.org/html/draft-tamplin-hybi-google-mux-02#section-8
            conn1 = client.open(serverUri,new NoopWebSocket(),2,TimeUnit.SECONDS);
            conn2 = client.open(serverUri,new NoopWebSocket(),2,TimeUnit.SECONDS);
            conn1.sendMessage("Hello");
            conn2.sendMessage("bye");
            conn1.sendMessage(" world");

            Assert.assertThat("Active server connection count",servlet.getConnectionCount(),is(1));

            Assert.assertThat("servlet",servlet.captures.size(),is(2));
        }
        finally
        {
            close(conn1);
            close(conn2);
        }
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
