package org.eclipse.jetty.websocket.extensions;

import org.eclipse.jetty.websocket.WebSocketClientFactory;

public interface ClientExtension extends Extension
{
    void setWebSocketClientFactory(WebSocketClientFactory factory);
}
