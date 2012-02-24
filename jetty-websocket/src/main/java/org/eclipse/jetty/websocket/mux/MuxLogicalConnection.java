/*******************************************************************************
 * Copyright (c) 1995-2012 Mort Bay Consulting Pty Ltd.
 * ======================================================================
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *   The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *
 *   The Apache License v2.0 is available at
 *   http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/
package org.eclipse.jetty.websocket.mux;

import java.io.IOException;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;

/**
 * MuxLogicalConnection is a logical connection for the {@link MuxExtension}, allowing WebSocket implementations access
 * to a connection object suitable for working with the specific {@link WebSocket}, without having access to the
 * physical Connection.
 */
public class MuxLogicalConnection implements Connection
{
    private static final Logger LOG = Log.getLogger(MuxLogicalConnection.class);
    private final int _channelNum;
    private final MuxedConnection _muxConnection;
    private final WebSocket _websocket;

    public MuxLogicalConnection(int channelNum, MuxedConnection muxConnection, WebSocket websocket)
    {
        this._channelNum = channelNum;
        this._muxConnection = muxConnection;
        this._websocket = websocket;
    }
    
    public WebSocket getWebSocket()
    {
        return _websocket;
    }

    public int getId()
    {
        return _channelNum;
    }

    public String getProtocol()
    {
        return _muxConnection.getProtocol();
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
        // TODO: manage logical isOpen
        return _muxConnection.isOpen();
    }

    public void setMaxIdleTime(int ms)
    {
        warnUnavailable("setMaxIdleTime(ms)");
    }

    public void setMaxTextMessageSize(int size)
    {
        warnUnavailable("setMaxTextMessageSize(size)");
    }

    public void setMaxBinaryMessageSize(int size)
    {
        warnUnavailable("setMaxBinaryMessageSize(size)");
    }

    public int getMaxIdleTime()
    {
        return _muxConnection.getMaxIdleTime();
    }

    public int getMaxTextMessageSize()
    {
        return _muxConnection.getMaxTextMessageSize();
    }

    public int getMaxBinaryMessageSize()
    {
        return _muxConnection.getMaxBinaryMessageSize();
    }

    private void warnUnavailable(String methodName)
    {
        LOG.warn("Method unavailable on logical mux connections: " + methodName);
    }
}
