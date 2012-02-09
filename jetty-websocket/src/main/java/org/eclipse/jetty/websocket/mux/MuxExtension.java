package org.eclipse.jetty.websocket.mux;

import org.eclipse.jetty.websocket.AbstractExtension;
import org.eclipse.jetty.websocket.Extension;

/**
 * 
 * 
 * @see <a href="http://tools.ietf.org/html/draft-tamplin-hybi-google-mux-02">draft-tamplin-hybi-google-mux-02</a>
 */
public class MuxExtension extends AbstractExtension implements Extension
{
    public MuxExtension(String name)
    {
        super("x-google-mux");
    }
    
    
}
