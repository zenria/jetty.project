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
