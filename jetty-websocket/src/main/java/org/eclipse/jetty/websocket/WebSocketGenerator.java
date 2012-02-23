/*******************************************************************************
 * Copyright (c) 2011 Intalio, Inc.
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
// ========================================================================
// Copyright (c) 2010 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at 
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses. 
// ========================================================================

package org.eclipse.jetty.websocket;

import java.io.IOException;

/**
 * WebSocketGenerator - responsible for creating appropriate outgoing WebSocket frames.
 */
public interface WebSocketGenerator
{
    /**
     * Flush the buffer (if any pending)
     * 
     * @return number of bytes flushed to outbound connection.
     * @throws IOException
     *             if underlying IO framework error during flush
     */
    int flush() throws IOException;

    /**
     * Test if buffer is empty.
     * 
     * @return true if buffer is non-null and contains data.
     */
    boolean isBufferEmpty();

    /**
     * Create outbound WebSocket frame.
     * 
     * @param flags
     *            the frame flags. currently only {@link WebSocketConnectionRFC6455#FLAG_FIN FIN} flag is supported (on
     *            or off).
     * @param opcode
     *            the frame opcode to use.
     * @param content
     *            the content byte buffer to send
     * @param offset
     *            the offset within the content to start sending from
     * @param length
     *            the number of bytes from content to send
     * @throws IOException
     *             if underlying IO framework error
     * @see <a href="http://tools.ietf.org/html/rfc6455#section-5.2">RFC 6455 - Sec 5.2 - Base Framing Protocol</a>
     */
    void addFrame(byte flags, byte opcode, byte[] content, int offset, int length) throws IOException;
}
