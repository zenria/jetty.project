package org.eclipse.jetty.websocket;

import java.net.ProtocolException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractWebSocketClientConnection implements Future<WebSocket.Connection>
{
    private final WebSocketClient _client;
    private WebSocketConnection _connection;
    private final CountDownLatch _done = new CountDownLatch(1);
    private Throwable _exception;
    private final URI _uri;
    private final WebSocket _websocket;

    public AbstractWebSocketClientConnection(WebSocket websocket, URI uri, WebSocketClient client)
    {
        _websocket = websocket;
        _uri = uri;
        _client = client;
    }

    public boolean cancel(boolean mayInterruptIfRunning)
    {
        try
        {
            synchronized (this)
            {
                if (!hasActiveConnection() && hasActiveChannel() && (_exception == null))
                {
                    closeChannel(WebSocketConnectionRFC6455.CLOSE_NO_CLOSE,"cancelled");
                }
            }
            return false;
        }
        finally
        {
            _done.countDown();
        }
    }

    public abstract void closeChannel(int closeCode, String message);

    public org.eclipse.jetty.websocket.WebSocket.Connection get() throws InterruptedException, ExecutionException
    {
        try
        {
            return get(Long.MAX_VALUE,TimeUnit.SECONDS);
        }
        catch (TimeoutException e)
        {
            throw new IllegalStateException("The universe has ended",e);
        }
    }

    public org.eclipse.jetty.websocket.WebSocket.Connection get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        _done.await(timeout,unit);

        org.eclipse.jetty.websocket.WebSocket.Connection connection = null;
        Throwable exception;
        synchronized (this)
        {
            exception = _exception;
            if (_connection == null)
            {
                exception = _exception;
            }
            else
            {
                connection = _connection.getConnection();
            }
        }

        if (hasActiveChannel())
        {
            closeChannel(WebSocketConnectionRFC6455.CLOSE_NO_CLOSE,"timeout");
        }

        if (exception != null)
        {
            throw new ExecutionException(exception);
        }

        if (connection != null)
        {
            return connection;
        }

        throw new TimeoutException();
    }

    public WebSocketClient getClient()
    {
        return _client;
    }

    public Map<String, String> getCookies()
    {
        return _client.getCookies();
    }

    public List<String> getExtensions()
    {
        return _client.getExtensions();
    }

    public MaskGen getMaskGen()
    {
        return _client.getMaskGen();
    }

    public int getMaxIdleTime()
    {
        return _client.getMaxIdleTime();
    }

    public String getOrigin()
    {
        return _client.getOrigin();
    }

    public String getProtocol()
    {
        return _client.getProtocol();
    }

    public URI getURI()
    {
        return _uri;
    }

    public WebSocket getWebSocket()
    {
        return _websocket;
    }

    public void handshakeFailed(Throwable ex)
    {
        try
        {
            synchronized (this)
            {
                if (hasActiveChannel())
                {
                    _exception = ex;

                    if (ex instanceof ProtocolException)
                    {
                        closeChannel(WebSocketConnectionRFC6455.CLOSE_PROTOCOL,ex.getMessage());
                    }
                    else
                    {
                        closeChannel(WebSocketConnectionRFC6455.CLOSE_NO_CLOSE,ex.getMessage());
                    }
                }
            }
        }
        finally
        {
            _done.countDown();
        }
    }

    public abstract boolean hasActiveChannel();

    public boolean hasActiveConnection()
    {
        return (_connection != null);
    }

    public boolean isCancelled()
    {
        synchronized (this)
        {
            return (!hasActiveChannel() && !hasActiveConnection());
        }
    }

    public boolean isDone()
    {
        synchronized (this)
        {
            return (hasActiveConnection() && (_exception == null));
        }
    }

    public void onConnection(WebSocketConnection connection)
    {
        try
        {
            _client.getFactory().addConnection(connection);

            connection.getConnection().setMaxTextMessageSize(_client.getMaxTextMessageSize());
            connection.getConnection().setMaxBinaryMessageSize(_client.getMaxBinaryMessageSize());

            WebSocketConnection con;
            synchronized (this)
            {
                if (hasActiveChannel())
                {
                    _connection = connection;
                }
                con = _connection;
            }

            if (con != null)
            {
                if (_websocket instanceof WebSocket.OnFrame)
                {
                    ((WebSocket.OnFrame)_websocket).onHandshake((WebSocket.FrameConnection)con.getConnection());
                }

                _websocket.onOpen(con.getConnection());
            }
        }
        finally
        {
            _done.countDown();
        }
    }

    @Override
    public String toString()
    {
        return "[" + _uri + "," + _websocket + "]@" + hashCode();
    }
}
