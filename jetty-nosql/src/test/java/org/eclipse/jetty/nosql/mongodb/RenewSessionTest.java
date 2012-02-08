package org.eclipse.jetty.nosql.mongodb;

import org.eclipse.jetty.server.session.AbstractRenewSessionTest;
import org.eclipse.jetty.server.session.AbstractTestServer;
import org.junit.Ignore;
import org.junit.Test;

public class RenewSessionTest extends AbstractRenewSessionTest
{

    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return  new MongoTestServer(port,max,scavenge);
    }

    @Test
    @Ignore ("requires mongodb server")
    public void testRenewedSession() throws Exception
    {
        // TODO Auto-generated method stub
        super.testRenewedSession();
    }


}
