package org.eclipse.jetty.websocket.helper;

import org.eclipse.jetty.websocket.WebSocket;

/**
 * A do nothing WebSocket.
 */
public class NoopWebSocket implements WebSocket
{
    public void onOpen(Connection connection)
    {
        /* ignore */
    }

    public void onClose(int closeCode, String message)
    {
        /* ignore */
    }
}
