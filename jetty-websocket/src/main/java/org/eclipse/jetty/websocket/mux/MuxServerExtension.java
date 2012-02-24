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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;
import org.eclipse.jetty.websocket.extensions.AbstractExtension;
import org.eclipse.jetty.websocket.extensions.ServerExtension;

/**
 * Server specific version of the MuxExtension.
 */
public class MuxServerExtension extends AbstractExtension implements ServerExtension
{
    private WebSocketFactory.Acceptor _acceptor;
    private HttpServletRequest _request;
    private String _protocol;

    public MuxServerExtension(MuxExtension muxBase)
    {
        super(muxBase.getName());
        Map<String, String> params = muxBase.getInitParameters();
        params.put("jetty-mode","server");
        init(params);
    }

    public void onWebSocketServerFactory(WebSocketFactory factory)
    {
        this._acceptor = factory.getAcceptor();
    }

    public void onWebSocketCreation(WebSocket websocket, HttpServletRequest request, String protocol)
    {
        this._request = request;
        this._protocol = protocol;
    }

    @Override
    public WebSocket bindWebSocket(WebSocket websocket)
    {
        if (websocket instanceof MuxedServerWebSocket)
        {
            return websocket;
        }
        return new MuxedServerWebSocket(websocket,_acceptor,_request,_protocol);
    }
}
