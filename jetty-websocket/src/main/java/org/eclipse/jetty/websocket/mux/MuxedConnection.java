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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;

/**
 * The actual Mux connection, responsible for doing the routing to/from {@link MuxLogicalConnection}.
 * <p>
 * The role of this connection is to provide the muxing of the connections, it has no responsibility to manage the
 * creation of new {@link WebSocket} implementations. That is left up to the mode specific implementations
 * {@link MuxedServerConnection} and {@link MuxedClientConnection}.
 */
public abstract class MuxedConnection implements Connection, WebSocket
{
    private static final Logger LOG = Log.getLogger(MuxedConnection.class);
    /**
     * Representation of mux LogicalConnections, where the index is the channel number.
     * <p>
     * Channel 0 is a special channel reserved for a control channel.
     */
    private List<MuxLogicalConnection> _logicalConnections;
    private Connection _physicalConnection;

    public MuxedConnection(Connection physicalConnection)
    {
        this._physicalConnection = physicalConnection;
        this._logicalConnections = new ArrayList<MuxLogicalConnection>();
    }

    public MuxLogicalConnection addLogicalChannel(int channelNum, WebSocket websocket)
    {
        synchronized (_logicalConnections)
        {
            LOG.debug("Adding logical channel: " + channelNum + " for " + websocket);
            // Ensure enough space in arraylist
            while (channelNum >= _logicalConnections.size())
            {
                _logicalConnections.add(null);
            }
            
            MuxLogicalConnection connection = new MuxLogicalConnection(channelNum,this,websocket);
            _logicalConnections.set(channelNum,connection);
            LOG.info("Adding Logical Channel " + channelNum + ": " + connection);
            return connection;
        }
    }
    
    public void onOpen(Connection connection)
    {
        // TODO Auto-generated method stub
        
    }

    public void onClose(int closeCode, String message)
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

    public void disconnect()
    {
        // TODO Auto-generated method stub

    }

    public int getMaxBinaryMessageSize()
    {
        return _physicalConnection.getMaxBinaryMessageSize();
    }

    public int getMaxIdleTime()
    {
        return _physicalConnection.getMaxIdleTime();
    }

    public int getMaxTextMessageSize()
    {
        return _physicalConnection.getMaxTextMessageSize();
    }

    public String getProtocol()
    {
        return _physicalConnection.getProtocol();
    }

    public boolean isOpen()
    {
        return _physicalConnection.isOpen();
    }

    public MuxLogicalConnection removeLogicalChannel(int channelNum)
    {
        synchronized (_logicalConnections)
        {
            LOG.debug("Removing logical channel: " + channelNum);
            if (_logicalConnections.size() > channelNum)
            {
                return null;
            }
            MuxLogicalConnection connection = _logicalConnections.get(channelNum);
            _logicalConnections.set(channelNum,null);
            return connection;
        }
    }

    public void sendMessage(byte[] data, int offset, int length) throws IOException
    {
        // TODO Auto-generated method stub

    }

    public void sendMessage(String data) throws IOException
    {
        // TODO Auto-generated method stub

    }

    public void setMaxBinaryMessageSize(int size)
    {
        _physicalConnection.setMaxBinaryMessageSize(size);
    }

    public void setMaxIdleTime(int ms)
    {
        _physicalConnection.setMaxIdleTime(ms);
    }

    public void setMaxTextMessageSize(int size)
    {
        _physicalConnection.setMaxTextMessageSize(size);
    }

    protected int requestUnusedChannelNum()
    {
        // TODO: find unused channel number, reserve it, and return it.
        // TODO: consider having something like PendingConnection object to hold this state?
        return 0;
    }

    /**
     * Return a channel number as unreserved.
     * @param channelNum
     */
    protected void relenquishChannelNum(int channelNum)
    {
        
    }
}
