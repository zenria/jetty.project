/*******************************************************************************
 * Copyright (c) 2011 Intalio, Inc.
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* ------------------------------------------------------------ */
/**
 * <p>
 * {@link WebSocketClient} allows to create multiple connections to multiple destinations that can speak the websocket
 * protocol.
 * </p>
 * <p>
 * When creating websocket connections, {@link WebSocketClient} accepts a {@link WebSocket} object (to receive events
 * from the server), and returns a {@link WebSocket.Connection} to send data to the server.
 * </p>
 * <p>
 * Example usage is as follows:
 * </p>
 * 
 * <pre>
 * WebSocketClientFactory factory = new WebSocketClientFactory();
 * factory.start();
 * 
 * WebSocketClient client = factory.newWebSocketClient();
 * // Configure the client
 * 
 * WebSocket.Connection connection = client.open(new URI(&quot;ws://127.0.0.1:8080/&quot;),new WebSocket.OnTextMessage()
 * {
 *     public void onOpen(Connection connection)
 *     {
 *         // open notification
 *     }
 * 
 *     public void onClose(int closeCode, String message)
 *     {
 *         // close notification
 *     }
 * 
 *     public void onMessage(String data)
 *     {
 *         // handle incoming message
 *     }
 * }).get(5,TimeUnit.SECONDS);
 * 
 * connection.sendMessage(&quot;Hello World&quot;);
 * </pre>
 */
public class WebSocketClient
{
    private final WebSocketClientFactory _factory;
    private final Map<String, String> _cookies = new ConcurrentHashMap<String, String>();
    private final List<String> _requestedExtensions = new CopyOnWriteArrayList<String>();
    private String _origin;
    private String _protocol;
    private int _maxIdleTime = -1;
    private int _maxTextMessageSize = 16 * 1024;
    private int _maxBinaryMessageSize = -1;
    private MaskGen _maskGen;
    private SocketAddress _bindAddress;

    /* ------------------------------------------------------------ */
    /**
     * <p>
     * Creates a WebSocketClient from a private WebSocketClientFactory.
     * </p>
     * <p>
     * This can be wasteful of resources if many clients are created.
     * </p>
     * 
     * @deprecated Use {@link WebSocketClientFactory#newWebSocketClient()}
     * @throws Exception
     *             if the private WebSocketClientFactory fails to start
     */
    @Deprecated
    public WebSocketClient() throws Exception
    {
        _factory = new WebSocketClientFactory();
        _factory.start();
        _maskGen = _factory.getMaskGen();
    }

    /* ------------------------------------------------------------ */
    /**
     * <p>
     * Creates a WebSocketClient with shared WebSocketClientFactory.
     * </p>
     * 
     * @param factory
     *            the shared {@link WebSocketClientFactory}
     */
    public WebSocketClient(WebSocketClientFactory factory)
    {
        _factory = factory;
        _maskGen = _factory.getMaskGen();
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The WebSocketClientFactory this client was created with.
     */
    public WebSocketClientFactory getFactory()
    {
        return _factory;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return the address to bind the socket channel to
     * @see #setBindAddress(SocketAddress)
     */
    public SocketAddress getBindAddress()
    {
        return _bindAddress;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param bindAddress
     *            the address to bind the socket channel to
     * @see #getBindAddress()
     */
    public void setBindAddress(SocketAddress bindAddress)
    {
        this._bindAddress = bindAddress;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The maxIdleTime in ms for connections opened by this client, or -1 if the default from
     *         {@link WebSocketClientFactory#getSelectorManager()} is used.
     * @see #setMaxIdleTime(int)
     */
    public int getMaxIdleTime()
    {
        return _maxIdleTime;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param maxIdleTime
     *            The max idle time in ms for connections opened by this client
     * @see #getMaxIdleTime()
     */
    public void setMaxIdleTime(int maxIdleTime)
    {
        _maxIdleTime = maxIdleTime;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The subprotocol string for connections opened by this client.
     * @see #setProtocol(String)
     */
    public String getProtocol()
    {
        return _protocol;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param protocol
     *            The subprotocol string for connections opened by this client.
     * @see #getProtocol()
     */
    public void setProtocol(String protocol)
    {
        _protocol = protocol;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The origin URI of the client
     * @see #setOrigin(String)
     */
    public String getOrigin()
    {
        return _origin;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param origin
     *            The origin URI of the client (eg "http://example.com")
     * @see #getOrigin()
     */
    public void setOrigin(String origin)
    {
        _origin = origin;
    }

    /* ------------------------------------------------------------ */
    /**
     * <p>
     * Returns the map of the cookies that are sent during the initial HTTP handshake that upgrades to the websocket
     * protocol.
     * </p>
     * 
     * @return The read-write cookie map
     */
    public Map<String, String> getCookies()
    {
        return _cookies;
    }

    /* ------------------------------------------------------------ */
    /**
     * The list of extension names (and optional parameters) that will be issued to the websocket server as part of the
     * WebSocket request for upgrade/handshake.
     * <p>
     * See the {@link WebSocketConnection#getExtensions()} for list of actual extensions in use by the connection (as a
     * direct result of the websocket handshake response)
     * 
     * @return The list of websocket protocol extension names (and optional parameters)
     */
    public List<String> getExtensions()
    {
        return _requestedExtensions;
    }

    /* ------------------------------------------------------------ */
    /**
     * Add an extension to the list of extensions.
     * <p>
     * The name (and optional parameters) is added to the end of the list of extensions, regardless if it exists or not.
     * <p>
     * Note: the parameters must be properly formatted to function properly. See <a
     * href="http://tools.ietf.org/html/rfc6455#section-9.1">RFC 6455 - Sec 9.1 - Negotiating Extensions</a> for
     * details.
     * <p>
     * Optionally use the {@link #addExtension(String, Map)} method to allow for proper parameter formatting.
     * <p>
     * Examples:
     * 
     * <pre>
     * // Valid Syntax
     * addExtension(&quot;mux&quot;);
     * addExtension(&quot;identity; id=debug&quot;);
     * addExtension(&quot;deflate&quot;);
     * 
     * // Invalid Syntax
     * addExtension(&quot;my extension&quot;); // spaces in extension name are not valid
     * addExtension(&quot;identity, deflate&quot;); // don't specify multiple extensions here
     * addExtension(&quot;deflate=gzip&quot;); // not how you specify parameters
     * </pre>
     * 
     * @param name
     *            the name of the extension to add
     * @see #addExtension(String, Map)
     */
    public void addExtension(String name)
    {
        _requestedExtensions.add(name);
    }

    /* ------------------------------------------------------------ */
    /**
     * Add an extension to the list of extensions, with parameters.
     * <p>
     * The name is added to the end if it doesn't exist already in the list of extensions.
     * <p>
     * If the name already exists, no change to the list of extensions is performed.
     * 
     * @param name
     *            the name of the extension to add
     * @param parameters
     *            the parameters for the extension
     */
    public void addExtension(String name, Map<String, String> parameters)
    {
        _requestedExtensions.add(name);
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the list of websocket extensions, in order desired.
     * 
     * @param names
     *            the names of the extensions to set
     */
    public void setExtensions(String... names)
    {
        synchronized (_requestedExtensions)
        {
            _requestedExtensions.clear();
            for (String name : names)
            {
                _requestedExtensions.add(name);
            }
        }
    }

    /* ------------------------------------------------------------ */
    /**
     * @return the mask generator to use, or null if not mask generator should be used
     * @see #setMaskGen(MaskGen)
     */
    public MaskGen getMaskGen()
    {
        return _maskGen;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param maskGen
     *            the mask generator to use, or null if not mask generator should be used
     * @see #getMaskGen()
     */
    public void setMaskGen(MaskGen maskGen)
    {
        _maskGen = maskGen;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The initial maximum text message size (in characters) for a connection
     */
    public int getMaxTextMessageSize()
    {
        return _maxTextMessageSize;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the initial maximum text message size for a connection. This can be changed by the application calling
     * {@link WebSocket.Connection#setMaxTextMessageSize(int)}.
     * 
     * @param maxTextMessageSize
     *            The default maximum text message size (in characters) for a connection
     */
    public void setMaxTextMessageSize(int maxTextMessageSize)
    {
        _maxTextMessageSize = maxTextMessageSize;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The initial maximum binary message size (in bytes) for a connection
     */
    public int getMaxBinaryMessageSize()
    {
        return _maxBinaryMessageSize;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the initial maximum binary message size for a connection. This can be changed by the application calling
     * {@link WebSocket.Connection#setMaxBinaryMessageSize(int)}.
     * 
     * @param maxBinaryMessageSize
     *            The default maximum binary message size (in bytes) for a connection
     */
    public void setMaxBinaryMessageSize(int maxBinaryMessageSize)
    {
        _maxBinaryMessageSize = maxBinaryMessageSize;
    }

    /* ------------------------------------------------------------ */
    /**
     * <p>
     * Opens a websocket connection to the URI and blocks until the connection is accepted or there is an error.
     * </p>
     * 
     * @param uri
     *            The URI to connect to.
     * @param websocket
     *            The {@link WebSocket} instance to handle incoming events.
     * @param maxConnectTime
     *            The interval to wait for a successful connection
     * @param units
     *            the units of the maxConnectTime
     * @return A {@link WebSocket.Connection}
     * @throws IOException
     *             if the connection fails
     * @throws InterruptedException
     *             if the thread is interrupted
     * @throws TimeoutException
     *             if the timeout elapses before the connection is completed
     * @see #open(URI, WebSocket)
     */
    public WebSocket.Connection open(URI uri, WebSocket websocket, long maxConnectTime, TimeUnit units) throws IOException, InterruptedException,
            TimeoutException
    {
        try
        {
            return open(uri,websocket).get(maxConnectTime,units);
        }
        catch (ExecutionException e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof IOException)
                throw (IOException)cause;
            if (cause instanceof Error)
                throw (Error)cause;
            if (cause instanceof RuntimeException)
                throw (RuntimeException)cause;
            throw new RuntimeException(cause);
        }
    }

    /* ------------------------------------------------------------ */
    /**
     * <p>
     * Asynchronously opens a websocket connection and returns a {@link Future} to obtain the connection.
     * </p>
     * <p>
     * The caller must call {@link Future#get(long, TimeUnit)} if they wish to impose a connect timeout on the open.
     * </p>
     * 
     * @param uri
     *            The URI to connect to.
     * @param websocket
     *            The {@link WebSocket} instance to handle incoming events.
     * @return A {@link Future} to the {@link WebSocket.Connection}
     * @throws IOException
     *             if the connection fails
     * @see #open(URI, WebSocket, long, TimeUnit)
     */
    public Future<WebSocket.Connection> open(URI uri, WebSocket websocket) throws IOException
    {
        if (!_factory.isStarted())
        {
            throw new IllegalStateException("Factory !started");
        }
        
        return _factory.requestConnection(this, uri, websocket);
    }

    public static InetSocketAddress toSocketAddress(URI uri)
    {
        String scheme = uri.getScheme();
        if (!("ws".equalsIgnoreCase(scheme) || "wss".equalsIgnoreCase(scheme)))
            throw new IllegalArgumentException("Bad WebSocket scheme: " + scheme);
        int port = uri.getPort();
        if (port == 0)
            throw new IllegalArgumentException("Bad WebSocket port: " + port);
        if (port < 0)
            port = "ws".equals(scheme)?80:443;

        return new InetSocketAddress(uri.getHost(),port);
    }
}
