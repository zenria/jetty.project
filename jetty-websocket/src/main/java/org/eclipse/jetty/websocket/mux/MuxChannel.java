package org.eclipse.jetty.websocket.mux;

/**
 * MuxChannel basics.
 * 
 * @see <a href="http://tools.ietf.org/html/draft-tamplin-hybi-google-mux-02">draft-tamplin-hybi-google-mux-02</a>
 */
public interface MuxChannel
{
    /**
     * New connection was opened.
     * 
     * @param channelId
     * @param connection
     */
    public void onMuxOpen(int channelId, MuxLogicalConnection connection);
    
    /**
     * Old connection was closed.
     * 
     * @param channelId
     * @param connection
     */
    public void onMuxClose(int channelId, MuxLogicalConnection connection);
    
    /**
     * Delivered raw data to the channel.
     * 
     * @param data the data
     * @param offset the offset within the data to pay attention to
     * @param length the length of the data concerned
     */
    public void onMuxData(int channelId, byte[] data, int offset, int length);
}
