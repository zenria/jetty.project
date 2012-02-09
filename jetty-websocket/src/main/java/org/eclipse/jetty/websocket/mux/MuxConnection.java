package org.eclipse.jetty.websocket.mux;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket.Connection;

/**
 * MuxConnection is a logic connection for the {@link MuxExtension}
 */
public class MuxConnection implements Connection
{
    public String getProtocol()
    {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return false;
    }

    public void setMaxIdleTime(int ms)
    {
        // TODO Auto-generated method stub
    }

    public void setMaxTextMessageSize(int size)
    {
        // TODO Auto-generated method stub
    }

    public void setMaxBinaryMessageSize(int size)
    {
        // TODO Auto-generated method stub
    }

    public int getMaxIdleTime()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxTextMessageSize()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxBinaryMessageSize()
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
