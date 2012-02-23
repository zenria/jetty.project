package org.eclipse.jetty.websocket.extensions;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;

/**
 * Server Specific Extension Point.
 */
public interface ServerExtension extends Extension
{
    /**
     * Notification about the server side {@link WebSocketFactory} in use.
     * 
     * @param factory
     *            the server side factory in use
     */
    void onWebSocketServerFactory(WebSocketFactory factory);

    /**
     * Notification of a Server side {@link WebSocket} creation details.
     * 
     * @param websocket
     *            the {@link WebSocket} implementation in use
     * @param request
     *            the request that created the {@link WebSocket} implementation
     * @param protocol
     *            the protocol that created the {@link WebSocket} implementation
     */
    void onWebSocketCreation(WebSocket websocket, HttpServletRequest request, String protocol);
}
