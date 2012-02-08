package org.eclipse.jetty.server.session;

import org.junit.Test;


public class RenewSessionTest extends AbstractRenewSessionTest
{

    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new JdbcTestServer(port,max,scavenge);
    }

    
    @Test
    public void testRenewedSession() throws Exception
    {
        super.testRenewedSession();
    }

    
}
