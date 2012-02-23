package org.eclipse.jetty.websocket.extensions;

import java.net.URI;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;

public interface ClientExtension extends Extension
{
    Connection establishConnection(URI uri, WebSocket websocket);
}
