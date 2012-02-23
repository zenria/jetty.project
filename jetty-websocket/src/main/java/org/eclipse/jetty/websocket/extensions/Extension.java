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
package org.eclipse.jetty.websocket.extensions;

import java.util.Map;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketGenerator;
import org.eclipse.jetty.websocket.WebSocketParser;

/**
 * Extension for WebSocket
 */
public interface Extension extends WebSocketParser.FrameHandler, WebSocketGenerator
{
    public enum Mode
    {
        SERVER, CLIENT
    }

    /**
     * The basic name of the extension, used in the negotiation of extensions during the WebSocket handshake.
     * <p>
     * This correlates with the <code>extension-token</code> in <a
     * href="http://tools.ietf.org/html/rfc6455#section-9.1">RFC 6455 Sec 9.1 - Negotiating Extensions</a>.
     * 
     * @return the extension-token name.
     */
    public String getName();

    /**
     * Once initialized, this is the entire (optionally) parameterized extension.
     * <p>
     * See <a href="http://tools.ietf.org/html/rfc6455#section-9.1">RFC 6445 Sec 9.1 - Negotiating Extensions</a>.
     * 
     * @return the Sec-WebSocketExtensions <code>extension</code> with optional <code>extension-param</code>
     */
    public String getParameterizedName();
    
    /**
     * Initialize / Configure the newly created extension.
     * 
     * @param parameters
     *            the configuration parameters.
     * @return true if initialized, false if extension is not properly initialized and should not be used.
     */
    public boolean init(Map<String, String> parameters);

    /**
     * Bind the extension to the specified connection, inbound frames, and outbound frames.
     * 
     * @param connection
     *            the frame connection to use
     * @param inbound
     *            the inbound frames
     * @param outbound
     *            the outbound frames
     */
    public void bind(WebSocket.FrameConnection connection, WebSocketParser.FrameHandler inbound, WebSocketGenerator outbound);

    /**
     * Bind the WebSocket being used
     * 
     * @param websocket
     *            the websocket being used for this instance of the extension.
     */
    public WebSocket bindWebSocket(WebSocket websocket);

    /**
     * Get the Extension implementation suitable for the mode that the extension will operate in.
     * <p>
     * For the vast majority of Extensions, returning <code>this</code> is acceptable.
     * <p>
     * However, for those extensions that require different logic depending on
     * 
     * @param mode
     *            the mode of operation for the extension
     * @return the extension implementation desired
     */
    public Extension getImplementation(Mode mode);
}
