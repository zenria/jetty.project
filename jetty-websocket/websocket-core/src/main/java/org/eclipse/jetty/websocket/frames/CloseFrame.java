package org.eclipse.jetty.websocket.frames;

import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.websocket.api.OpCode;
import org.eclipse.jetty.websocket.api.StatusCode;

/**
 * Representation of a <a href="https://tools.ietf.org/html/rfc6455#section-5.5.1">Close Frame (0x08)</a>.
 */
public class CloseFrame extends ControlFrame
{
    private short statusCode;
    private String reason;

    public CloseFrame()
    {
        this(StatusCode.NORMAL); // TODO: evaluate default (or unspecified status code)
    }

    public CloseFrame(short statusCode)
    {
        super(OpCode.CLOSE);
        this.statusCode = statusCode;
    }

    public String getReason()
    {
        return reason;
    }

    public short getStatusCode()
    {
        return statusCode;
    }

    public boolean hasReason()
    {
        return StringUtil.isBlank(reason);
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public void setStatusCode(short statusCode)
    {
        this.statusCode = statusCode;
    }

    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("CloseFrame[");
        b.append("len=").append(getPayloadLength());
        b.append(",statusCode=").append(statusCode);
        b.append(",reason=").append(reason);
        b.append("]");
        return b.toString();
    }
}
