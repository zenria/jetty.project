package org.eclipse.jetty.websocket;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.ByteChannel;
import java.util.concurrent.Future;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/* ------------------------------------------------------------ */
/**
 * The Future Websocket Connection.
 */
class WebSocketClientConnectionFuture extends AbstractWebSocketClientConnection implements Future<WebSocket.Connection>
{
    private static final Logger LOG = Log.getLogger(WebSocketClientConnectionFuture.class);
    ByteChannel _channel;

    protected WebSocketClientConnectionFuture(WebSocket websocket, URI uri, WebSocketClient client, ByteChannel channel)
    {
        super(websocket, uri, client);
        _channel = channel;
    }

    @Override
    public boolean hasActiveChannel()
    {
        return (_channel != null);
    }
    
    @Override
    public void closeChannel(int closeCode, String message)
    {
        closeChannel(_channel,closeCode,message);
        _channel = null;
    }
    
    private void closeChannel(ByteChannel channel, int code, String message)
    {
        try
        {
            getWebSocket().onClose(code,message);
        }
        catch (Exception e)
        {
            LOG.warn(e);
        }

        try
        {
            channel.close();
        }
        catch (IOException e)
        {
            LOG.debug(e);
        }
    }
}