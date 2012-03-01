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
package org.eclipse.jetty.websocket.mux;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.AbstractWebSocketClientConnection;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.eclipse.jetty.websocket.extensions.AbstractExtension;
import org.eclipse.jetty.websocket.extensions.ClientExtension;

/**
 * Client implementation of the MuxExtension.
 */
public class MuxClientExtension extends AbstractExtension implements ClientExtension
{
    private static final Logger LOG = Log.getLogger(MuxClientExtension.class);

    public static class MuxConnectionHandler implements WebSocketClientFactory.EstablishConnectionHandler
    {
        /**
         * Per the section "Client Behavior" in the spec, the scheme://host:port must match the original connection in
         * order to be considered for use in mux.
         */
        private URI originalUri;

        public Future<Connection> establishConnection(WebSocketClient client, URI uri, WebSocket websocket) throws IOException
        {
            // Per the mux spec, Sec 9 (Client Behavior) we should only allow connections on the same
            // scheme://host:port as the original connection.

            if (!uri.getScheme().equalsIgnoreCase(originalUri.getScheme()))
            {
                LOG.debug("Not using muxed connection, scheme mismatch. original uri [" + originalUri + "] request uri [" + uri + "]");
                return null;
            }
            if (!uri.getHost().equalsIgnoreCase(originalUri.getHost()))
            {
                LOG.debug("Not using muxed connection, host mismatch. original uri [" + originalUri + "] request uri [" + uri + "]");
                return null;
            }
            if (uri.getPort() != originalUri.getPort())
            {
                LOG.debug("Not using muxed connection, port mismatch. original uri [" + originalUri + "] request uri [" + uri + "]");
                return null;
            }

            // TODO: Initiate a future connection using mux connection and AddChannel.

            return null;
        }
    }

    public static class MuxAddChannelFuture extends AbstractWebSocketClientConnection
    {
        private MuxedClientConnection _mux;

        protected MuxAddChannelFuture(WebSocket websocket, URI uri, WebSocketClient client)
        {
            super(websocket, uri, client);
        }
     
        @Override
        public boolean hasActiveChannel()
        {
            return (_mux != null);
        }
        
        @Override
        public void closeChannel(int closeCode, String message)
        {
            _mux.close(closeCode,message);
        }
        
        @Override
        public boolean hasActiveConnection()
        {
            return (_mux != null);
        }
    }

    public MuxClientExtension(MuxExtension muxBase)
    {
        super(muxBase.getName());
        Map<String, String> params = muxBase.getInitParameters();
        params.put("jetty-mode","client");
        init(params);
    }

    @Override
    public WebSocket bindWebSocket(WebSocket websocket)
    {
        if (websocket instanceof MuxedWebSocket)
        {
            return websocket;
        }
        return new MuxedWebSocket(websocket);
    }

    public void setWebSocketClientFactory(WebSocketClientFactory factory)
    {
        // TODO Auto-generated method stub

    }
}
