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

import java.net.URI;
import java.util.Map;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.extensions.AbstractExtension;
import org.eclipse.jetty.websocket.extensions.ClientExtension;

/**
 * Client implementation of the MuxExtension.
 */
public class MuxClientExtension extends AbstractExtension implements ClientExtension
{
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

    public Connection establishConnection(URI uri, WebSocket websocket)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
