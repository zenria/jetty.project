package org.eclipse.jetty.websocket.mux;

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
        init(muxBase.getInitParameters());
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
