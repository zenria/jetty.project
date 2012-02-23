package org.eclipse.jetty.websocket;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.WebSocketParser.FrameHandler;
import org.eclipse.jetty.websocket.extensions.AbstractExtension;
import org.eclipse.jetty.websocket.extensions.Extension;

public class DebugExtension extends AbstractExtension implements Extension
{
    private static final Logger LOG = Log.getLogger(DebugExtension.class);
    private String id;
    private static StringWriter capture = new StringWriter();

    public DebugExtension()
    {
        super("debug");
    }

    @Override
    public boolean init(Map<String, String> parameters)
    {
        super.init(parameters);
        id = getInitParameter("id");
        return true;
    }

    public static String getCapture()
    {
        synchronized (capture)
        {
            capture.flush();
            return capture.toString();
        }
    }

    public static void setCapture(String s)
    {
        synchronized (capture)
        {
            capture = new StringWriter();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see FrameHandler#onFrame(byte, byte, Buffer)
     */
    @Override
    public void onFrame(byte flags, byte opcode, Buffer buffer)
    {
        LOG.debug("[" + getParameterizedName() + "] onFrame(" + flags + ", " + opcode + ", buffer)");
        synchronized (capture)
        {
            capture.append("in[");
            switch (opcode)
            {
                case WebSocketConnectionRFC6455.OP_TEXT:
                    capture.append("t");
                    break;
                case WebSocketConnectionRFC6455.OP_CLOSE:
                    capture.append("c");
                    break;
                case WebSocketConnectionRFC6455.OP_BINARY:
                    capture.append("b");
                    break;
            }
            capture.write(id);
            capture.write("]");
            capture.flush();
        }
        super.onFrame(flags,opcode,buffer);
    }

    /**
     * {@inheritDoc}
     * 
     * @see WebSocketGenerator#addFrame(byte, byte, byte[], int, int)
     */
    @Override
    public void addFrame(byte flags, byte opcode, byte[] content, int offset, int length) throws IOException
    {
        LOG.debug("[" + getParameterizedName() + "] addFrame(" + flags + ", " + opcode + ", content, " + offset + ", " + length + ")");
        synchronized (capture)
        {
            capture.append("out[");
            switch (opcode)
            {
                case WebSocketConnectionRFC6455.OP_TEXT:
                    capture.append("t");
                    break;
                case WebSocketConnectionRFC6455.OP_CLOSE:
                    capture.append("c");
                    break;
                case WebSocketConnectionRFC6455.OP_BINARY:
                    capture.append("b");
                    break;
            }
            capture.append(id).append("]");
            capture.flush();
        }
        super.addFrame(flags,opcode,content,offset,length);
    }
}
