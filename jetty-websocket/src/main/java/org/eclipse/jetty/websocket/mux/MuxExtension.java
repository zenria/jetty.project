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
