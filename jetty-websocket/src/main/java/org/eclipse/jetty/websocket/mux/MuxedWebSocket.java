package org.eclipse.jetty.websocket.mux;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;

/**
 * Represents the physical websocket in a mux extension socket scenario.
 * <p>
 * Also responsible for processing the Mux channel 0 control channel.
 */
public class MuxedWebSocket implements MuxChannel, WebSocket, WebSocket.OnFrame, WebSocket.OnBinaryMessage, WebSocket.OnTextMessage, WebSocket.OnControl
{
    /**
     * Represents the implemented acceptor that handles the creation of new WebSocket implementations that become bound
     * to a specific channel.
     */
    private WebSocketFactory.Acceptor _acceptor;

    /**
     * The list of channels.
     */
    private List<MuxChannel> _channels;

    /**
     * The physical websocket connection.
     */
    private Connection _physicalConnection;

    public MuxedWebSocket(WebSocketFactory.Acceptor acceptor)
    {
        _channels = new ArrayList<MuxChannel>();
    }

    public void onClose(int closeCode, String message)
    {
        this._physicalConnection = null;
        // TODO: close all muxed channels.
    }

    public boolean onControl(byte controlCode, byte[] data, int offset, int length)
    {
        // DO NOT pass control frames to muxed channels
        // TODO Auto-generated method stub
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
    public void onMuxOpen(int channelId, MuxConnection connection)
    {
        // Ignore, not possible in MUX
    }

    /**
     * Control Channel Close
     */
    public void onMuxClose(int channelId, MuxConnection connection)
    {
        // Ignore, not possible in MUX
    }
}
