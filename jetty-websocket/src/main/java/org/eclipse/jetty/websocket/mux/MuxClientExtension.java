package org.eclipse.jetty.websocket.mux;

import org.eclipse.jetty.websocket.WebSocket;
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
        init(muxBase.getInitParameters());
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
}
