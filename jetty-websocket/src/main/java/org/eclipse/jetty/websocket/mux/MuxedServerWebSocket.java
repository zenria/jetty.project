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

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;
import org.eclipse.jetty.websocket.WebSocketFactory.Acceptor;
import org.eclipse.jetty.websocket.WebSocketServlet;

/**
 * Similar to MuxedWebSocket, but for Server-side that requires management of new {@link WebSocket} implementations from
 * the {@link WebSocketServlet#doWebSocketConnect(HttpServletRequest, String)} call.
 */
public class MuxedServerWebSocket extends MuxedWebSocket
{
    /**
     * Represents the implemented acceptor that is used for creation of new WebSocket implementations that will
     * eventually become bound to a specific mux channel.
     */
    private WebSocketFactory.Acceptor _acceptor;

    /**
     * Original HttpServletRequest, required for new WebSocket implementations tied to new mux channels.
     */
    private HttpServletRequest _originalRequest;

    /**
     * Original WebSocket protocol, required for new WebSocket implementations tied to new mux channels.
     */
    private String _originalProtocol;

    public MuxedServerWebSocket(WebSocket channel1, Acceptor acceptor, HttpServletRequest request, String protocol)
    {
        super(channel1);
        this._acceptor = acceptor;
        this._originalRequest = request;
        this._originalProtocol = protocol;
    }
}
