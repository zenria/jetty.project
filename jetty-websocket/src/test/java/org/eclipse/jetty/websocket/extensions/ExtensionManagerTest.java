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
package org.eclipse.jetty.websocket.extensions;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ExtensionManagerTest
{
    @Test
    public void testParameterizeNameNullParams()
    {
        Assert.assertEquals("mux",ExtensionManager.parameterize("mux",null));
    }

    @Test
    public void testParameterizeNameOneParam()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode","debug");
        Assert.assertEquals("identity;mode=debug",ExtensionManager.parameterize("identity",params));
    }

    @Test
    public void testParameterizeNameEmptyParams()
    {
        Map<String, String> params = new HashMap<String, String>();
        Assert.assertEquals("identity",ExtensionManager.parameterize("identity",params));
    }

    @Test
    public void testParameterizeNameTwoParamSimple()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode","debug");
        params.put("id","2");
        Assert.assertEquals("identity;mode=debug;id=2",ExtensionManager.parameterize("identity",params));
    }

    @Test
    public void testParameterizeNameOneParamWithSpaces()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode","old version");
        Assert.assertEquals("identity;mode=\"old version\"",ExtensionManager.parameterize("identity",params));
    }

    @Test
    public void testParameterizeNameOneParamWithEquals()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode","pie=apple");
        Assert.assertEquals("identity;mode=\"pie=apple\"",ExtensionManager.parameterize("identity",params));
    }

    @Test
    public void testParameterizeNameOneParamWithSemicolon()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode","rest;some");
        Assert.assertEquals("identity;mode=\"rest;some\"",ExtensionManager.parameterize("identity",params));
    }
}
