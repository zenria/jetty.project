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
