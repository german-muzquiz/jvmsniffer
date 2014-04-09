package com.jvmsniffer;


import com.jvmsniffer.connections.SnifferHttpURLConnection;
import com.jvmsniffer.connections.SnifferHttpsURLConnection;
import com.jvmsniffer.connections.SnifferURLConnection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;


public class SnifferURLStreamHandler extends URLStreamHandler
{
    private URLStreamHandler itsRealHandler;
    private String itsProtocol;


    public SnifferURLStreamHandler(URLStreamHandler aRealHandler, String aProtocol)
    {
        itsRealHandler = aRealHandler;
        itsProtocol = aProtocol;
    }


    @Override
    protected URLConnection openConnection(URL aUrl) throws IOException
    {
        try
        {
            Method myMethod = URLStreamHandler.class.getDeclaredMethod("openConnection", URL.class);
            myMethod.setAccessible(true);
            URLConnection myRealConnection = (URLConnection) myMethod.invoke(itsRealHandler, aUrl);

            if(itsProtocol.equalsIgnoreCase("http"))
            {
                return new SnifferHttpURLConnection(aUrl, (java.net.HttpURLConnection) myRealConnection);
            }
            else if(itsProtocol.equalsIgnoreCase("https"))
            {
                return new SnifferHttpsURLConnection(aUrl, (javax.net.ssl.HttpsURLConnection) myRealConnection);
            }
            else
            {
                return new SnifferURLConnection(aUrl, myRealConnection);
            }
        }
        catch (NoSuchMethodException anEx)
        {
            throw new IOException(anEx);
        }
        catch (InvocationTargetException anEx)
        {
            throw new IOException(anEx);
        }
        catch (IllegalAccessException anEx)
        {
            throw new IOException(anEx);
        }
    }

}
