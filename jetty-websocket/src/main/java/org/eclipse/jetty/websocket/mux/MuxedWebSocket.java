package org.eclipse.jetty.websocket.mux;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.WebSocket;

/**
 * Represents the physical websocket in a mux extension socket scenario.
 * <p>
 * Also responsible for processing the Mux channel 0 control channel.
 */
public class MuxedWebSocket implements MuxChannel, WebSocket, WebSocket.OnFrame, WebSocket.OnBinaryMessage, WebSocket.OnTextMessage, WebSocket.OnControl
{
    private static final Logger LOG = Log.getLogger(MuxedWebSocket.class);
    
    /**
     * The list of channels.
     */
    private List<MuxChannel> _channels;

    /**
     * The physical websocket connection.
     */
    private Connection _physicalConnection;

    public MuxedWebSocket(WebSocket channel1)
    {
        _channels = new ArrayList<MuxChannel>();
        setChannel(0,this); // Channel 0
        setChannel(1,new MuxSubChannel(channel1)); // Channel 1
    }

    private void setChannel(int channelNum, MuxChannel channel)
    {
        synchronized (_channels)
        {
            // Enough space in arraylist?
            while (channelNum >= _channels.size())
            {
                _channels.add(null);
            }
            _channels.set(channelNum,channel);
            LOG.info("Adding Channel " + channelNum + ": " + channel);
        }
    }

    public void onClose(int closeCode, String message)
    {
        this._physicalConnection = null;

        // close other channels
        synchronized (_channels)
        {
            int len = _channels.size();
            for (int id = len-1; id >= 0; id--)
            {
                MuxChannel channel = _channels.get(id);
//                MuxLogicalConnection conn = new MuxLogicalConnection(id,_physicalConnection);
//                conn.close();
//                channel.onMuxClose(id,conn);
                _channels.remove(id);
            }
        }
    }

    public boolean onControl(byte controlCode, byte[] data, int offset, int length)
    {
        // DO NOT pass control frames to muxed channels
        return false;
    }

    public boolean onFrame(byte flags, byte opcode, byte[] data, int offset, int length)
    {
        // TODO Determine scope / requirement for mux
        return false;
    }

    public void onHandshake(FrameConnection connection)
    {
        // TODO Determine scope / requirement for mux
    }

    public void onMessage(byte[] data, int offset, int length)
    {
        // Binary message - the expected format for all mux physical connection traffic.

        // TODO Determine scope / requirement for mux
    }

    public void onMessage(String data)
    {
        // Text message - unexpected format for mux traffic on physical connection.

        // TODO Determine scope / requirement for mux
    }

    public void onOpen(Connection connection)
    {
        this._physicalConnection = connection;
        // Do not pass this connection object to the lower channels.

        // Create channel 0 (mux control channel)
        this._channels.set(0,this);

        // Update other channels
        synchronized (_channels)
        {
            int len = _channels.size();
            for (int id = 1; id < len; id++)
            {
                MuxChannel channel = _channels.get(id);
//                channel.onMuxOpen(id,new MuxLogicalConnection(id,this._physicalConnection));
            }
        }
    }

    /**
     * Control Channel MUX Data
     */
    public void onMuxData(int channelId, byte[] data, int offset, int length)
    {
        // TODO Auto-generated method stub
    }

    /**
     * Control Channel Open
     */
    public void onMuxOpen(int channelId, MuxLogicalConnection connection)
    {
        // Ignore, not possible in MUX
    }

    /**
     * Control Channel Close
     */
    public void onMuxClose(int channelId, MuxLogicalConnection connection)
    {
        // Ignore, not possible in MUX
    }
}
