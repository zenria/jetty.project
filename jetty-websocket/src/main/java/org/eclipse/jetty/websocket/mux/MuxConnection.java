package org.eclipse.jetty.websocket.mux;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket.Connection;

/**
 * MuxConnection is a logical connection for the {@link MuxExtension}, allowing WebSocket implementations access to a
 * connection object suitable for working with the specific {@link MuxChannel}, without having access to the physical
 * Connection.
 */
public class MuxConnection implements Connection
{
    private final int _id;
    private final Connection _physicalConnection;

    public MuxConnection(int id, Connection physicalConnection)
    {
        this._id = id;
        this._physicalConnection = physicalConnection;
    }

    public int getId()
    {
        return _id;
    }

    public String getProtocol()
    {
        return _physicalConnection.getProtocol();
    }

    public void sendMessage(String data) throws IOException
    {
        // TODO Auto-generated method stub
    }

    public void sendMessage(byte[] data, int offset, int length) throws IOException
    {
        // TODO Auto-generated method stub
    }

    public void disconnect()
    {
        // TODO Auto-generated method stub
    }

    public void close()
    {
        // TODO Auto-generated method stub
    }

    public void close(int closeCode, String message)
    {
        // TODO Auto-generated method stub
    }

    public boolean isOpen()
    {
        return _physicalConnection.isOpen();
    }

    public void setMaxIdleTime(int ms)
    {
        _physicalConnection.setMaxIdleTime(ms);
    }

    public void setMaxTextMessageSize(int size)
    {
        _physicalConnection.setMaxTextMessageSize(size);
    }

    public void setMaxBinaryMessageSize(int size)
    {
        _physicalConnection.setMaxBinaryMessageSize(size);
    }

    public int getMaxIdleTime()
    {
        return _physicalConnection.getMaxIdleTime();
    }

    public int getMaxTextMessageSize()
    {
        return _physicalConnection.getMaxTextMessageSize();
    }

    public int getMaxBinaryMessageSize()
    {
        return _physicalConnection.getMaxBinaryMessageSize();
    }
}
