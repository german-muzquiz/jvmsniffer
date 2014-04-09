package com.jvmsniffer;

import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class SnifferURLStreamHandlerFactory implements java.net.URLStreamHandlerFactory
{
    private static final Logger LOG = Logger.getLogger(SnifferURLStreamHandlerFactory.class.getName());

    // package name of the Sun implementation protocol handlers
    private static final String SUN_PACKAGE_PREFIX =  "sun.net.www.protocol";

    /**
     * The property which specifies the package prefix list to be scanned
     * for protocol handlers.  The value of this property (if any) should
     * be a vertical bar delimited list of package names to search through
     * for a protocol handler to load.  The policy of this class is that
     * all protocol handlers will be in a class called <protocolname>.Handler,
     * and each package in the list is examined in turn for a matching
     * handler.  If none are found (or the property is not specified), the
     * default package prefix, sun.net.www.protocol, is used.  The search
     * proceeds from the first package in the list to the last and stops
     * when a match is found.
     */
    private static final String protocolPathProp = "java.protocol.handler.pkgs";

    // Sun classpath handlers. Initialized once at startup
    private final String[] itsJvmPkgs;

    private Map<String, URLStreamHandler> itsHandlers;



    public SnifferURLStreamHandlerFactory()
    {
        String packagePrefixList = System.getProperty(protocolPathProp, "");

        if (!packagePrefixList.equals(""))
        {
            packagePrefixList += "|";
        }

        packagePrefixList += SUN_PACKAGE_PREFIX + "|";
        itsJvmPkgs = packagePrefixList.split("\\|");
        itsHandlers = new HashMap<String, URLStreamHandler>();
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String aProtocol)
    {
        if(itsHandlers.get(aProtocol) == null)
        {
            URLStreamHandler myHandler = getSunHandler(aProtocol);

            if(myHandler != null)
            {
                itsHandlers.put(aProtocol, myHandler);
            }
            else
            {
                myHandler = getAndroidHandler(aProtocol);
                if(myHandler != null)
                {
                    itsHandlers.put(aProtocol, myHandler);
                }
            }
        }

        URLStreamHandler myHandler = itsHandlers.get(aProtocol);

        if(myHandler != null)
        {
            LOG.info("Sniffing on protocol: " + aProtocol);
            return new SnifferURLStreamHandler(myHandler, aProtocol);
        }
        else
        {
            LOG.info("No URLStreamHandler found for protocol " + aProtocol);
            return null;
        }
    }

    private URLStreamHandler getSunHandler(String aProtocol)
    {
        for(String myPackagePrefix : itsJvmPkgs)
        {
            myPackagePrefix = myPackagePrefix.trim();

            try
            {
                String clsName = myPackagePrefix + "." + aProtocol + ".Handler";
                LOG.info("Searching Sun handler: " + clsName);

                Class cls = null;

                try
                {
                    cls = Class.forName(clsName);
                }
                catch (ClassNotFoundException anEx)
                {
                    ClassLoader cl = ClassLoader.getSystemClassLoader();
                    if (cl != null)
                    {
                        cls = cl.loadClass(clsName);
                    }
                }

                if (cls != null)
                {
                    return (URLStreamHandler) cls.newInstance();
                }
            }
            catch (Exception e)
            {
                // any number of exceptions can get thrown here
            }
        }

        return null;
    }

    private URLStreamHandler getAndroidHandler(String aProtocol)
    {
        URLStreamHandler myHandler = null;

        try
        {
            Class myClass = null;

            if (aProtocol.equals("file"))
            {
                String name = "libcore.net.url.FileHandler";
                myClass = classForName(name);
            }
            else if (aProtocol.equals("ftp"))
            {
                String name = "libcore.net.url.FtpHandler";
                myClass = classForName(name);
            }
            else if (aProtocol.equals("jar"))
            {
                String name = "libcore.net.url.JarHandler";
                myClass = classForName(name);
            }
            else if (aProtocol.equals("http"))
            {
                String[] names = new String[]{"com.android.okhttp.HttpHandler", "libcore.net.http.HttpHandler"};
                for(String myName : names)
                {
                    myClass = classForName(myName);
                    if(myClass != null)
                    {
                        break;
                    }
                }
            }
            else if (aProtocol.equals("https"))
            {
                String[] names = new String[]{"com.android.okhttp.HttpsHandler", "libcore.net.http.HttpsHandler"};
                for(String myName : names)
                {
                    myClass = classForName(myName);
                    if(myClass != null)
                    {
                        break;
                    }
                }
            }

            if(myClass != null)
            {
                myHandler = (URLStreamHandler) myClass.newInstance();
            }

        }
        catch(Exception anEx)
        {
            // any number of exceptions can get thrown here
        }

        return myHandler;
    }

    private Class classForName(String aName)
    {
        try
        {
            LOG.info("Searching Android handler: " + aName);
            return Class.forName(aName);
        }
        catch(ClassNotFoundException anEx)
        {
            return null;
        }
    }

}
