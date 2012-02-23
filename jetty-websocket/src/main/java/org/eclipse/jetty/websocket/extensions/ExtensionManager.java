package org.eclipse.jetty.websocket.extensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.mux.MuxExtension;

public final class ExtensionManager
{
    private static final Logger LOG = Log.getLogger(ExtensionManager.class);
    private Map<String, Class<? extends Extension>> _registeredExtensions;

    public ExtensionManager()
    {
        _registeredExtensions = new HashMap<String, Class<? extends Extension>>();

        // predefined (shipped) extensions
        _registeredExtensions.put("identity",IdentityExtension.class);
        _registeredExtensions.put("fragment",FragmentExtension.class);
        _registeredExtensions.put("x-deflate-frame",DeflateFrameExtension.class);
        _registeredExtensions.put("mux",MuxExtension.class);
    }

    public Set<String> getRegisteredExtensionNames()
    {
        return _registeredExtensions.keySet();
    }

    public Map<String, Class<? extends Extension>> getRegisteredExtensions()
    {
        return _registeredExtensions;
    }

    public List<Extension> initExtensions(List<String> requested, Extension.Mode mode)
    {
        List<Extension> extensions = new ArrayList<Extension>();
        for (String rExt : requested)
        {
            QuotedStringTokenizer tok = new QuotedStringTokenizer(rExt,";");
            String extName = tok.nextToken().trim();
            Map<String, String> parameters = new HashMap<String, String>();
            while (tok.hasMoreTokens())
            {
                QuotedStringTokenizer nv = new QuotedStringTokenizer(tok.nextToken().trim(),"=");
                String name = nv.nextToken().trim();
                String value = nv.hasMoreTokens()?nv.nextToken().trim():null;
                parameters.put(name,value);
            }

            Extension extension = newExtensionImpl(extName);

            if (extension == null)
            {
                continue;
            }

            if (extension.init(parameters))
            {
                LOG.debug("add {} {}",extName,parameters);
                extensions.add(extension.getImplementation(mode));
            }
        }
        LOG.debug("extensions={}",extensions);
        return extensions;
    }

    private Extension newExtensionImpl(String name)
    {
        try
        {
            Class<? extends Extension> extClass = _registeredExtensions.get(name);
            if (extClass != null)
            {
                return extClass.newInstance();
            }
        }
        catch (Exception e)
        {
            LOG.warn(e);
        }

        return null;
    }

    public void registerExtension(String name, Class<? extends Extension> extensionClass)
    {
        _registeredExtensions.put(name,extensionClass);
    }
}
