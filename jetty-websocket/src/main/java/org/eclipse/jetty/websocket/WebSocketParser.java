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

import org.eclipse.jetty.io.Buffer;



/* ------------------------------------------------------------ */
/**
 * Parser the WebSocket protocol.
 *
 */
public interface WebSocketParser
{
    
    /**
     * Incoming Frame Handler
     */
    public interface FrameHandler
    {
        /**
         * Received processed WebSocket frame.
         * <p>
         * See <a href="http://tools.ietf.org/html/rfc6455#section-5.2">RFC 6455 - Sec 5.2 - Base Framing Protocol</a>
         * for details.
         * 
         * @param flags
         *            the raw frame flags. only the FIN bit is of value, the other 3 bits are marked as reserved and
         *            unused.
         * @param opcode
         *            the websocket opcode.
         * @param buffer
         *            the payload data
         */
        void onFrame(byte flags, byte opcode, Buffer buffer);

        /**
         * Received close opcode (0x08).
         * <p>
         * See <a href="http://tools.ietf.org/html/rfc6455#section-7.4">RFC 6455 - Sec 7.4 - Status Codes</a> for
         * details.
         * 
         * @param code
         *            the close code
         * @param message
         *            the optional message for the close message
         */
        void close(int code, String message);
    }

    Buffer getBuffer();

    /**
     * @return an indication of progress, normally bytes filled plus events parsed, or -1 for EOF
     */
    int parseNext();

    boolean isBufferEmpty();

    void fill(Buffer buffer);

}
