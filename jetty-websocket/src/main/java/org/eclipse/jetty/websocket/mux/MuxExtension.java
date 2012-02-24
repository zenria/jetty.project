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

import java.util.Map;

import org.eclipse.jetty.websocket.extensions.AbstractExtension;
import org.eclipse.jetty.websocket.extensions.Extension;

/**
 * Multiplex'd connections extension
 * 
 * @see <a href="http://tools.ietf.org/html/draft-tamplin-hybi-google-mux-02">draft-tamplin-hybi-google-mux-02</a>
 */
public class MuxExtension extends AbstractExtension implements Extension
{
    private Map<String, String> parameters;

    public MuxExtension()
    {
        super("mux");
    }

    @Override
    public Extension getImplementation(Mode mode)
    {
        if (mode == Mode.SERVER)
        {
            return new MuxServerExtension(this);
        }

        if (mode == Mode.CLIENT)
        {
            return new MuxClientExtension(this);
        }

        return this;
    }

    public Map<String, String> getInitParameters()
    {
        return parameters;
    }

    @Override
    public boolean init(Map<String, String> parameters)
    {
        this.parameters = parameters;
        return true;
    }
}
