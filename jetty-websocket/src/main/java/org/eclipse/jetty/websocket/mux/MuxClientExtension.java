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
