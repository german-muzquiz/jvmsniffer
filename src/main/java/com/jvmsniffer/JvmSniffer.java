package com.jvmsniffer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JvmSniffer
{
    private static final Logger LOG = Logger.getLogger(JvmSniffer.class.getName());
    private static PrintWriter itsLogWriter;

    private static final String[] PRINTABLE_CONTENT_TYPES = new String[]
    {
        "json",
        "text",
        "xml"
    };


    public static void init()
    {
        init(null);
    }

    public static void init(String aLogFile)
    {
        LOG.log(Level.INFO, "Loading JvmSniffer");

        if(aLogFile != null)
        {
            try
            {
                File myLogFile = new File(aLogFile);

                if(!myLogFile.exists())
                {
                    if(myLogFile.createNewFile())
                    {
                        itsLogWriter = new PrintWriter(new FileOutputStream(myLogFile, true));
                    }
                }
                else
                {
                    itsLogWriter = new PrintWriter(new FileOutputStream(myLogFile, true));
                }
            }
            catch (FileNotFoundException anEx) { /* ignore */ }
            catch (IOException anEx) { /* ignore */ }

            if(itsLogWriter != null)
            {
                LOG.info("Logging to file: " + aLogFile);
            }
        }

        URL.setURLStreamHandlerFactory(new SnifferURLStreamHandlerFactory());
    }

    public static void print(String aString)
    {
        if(itsLogWriter != null)
        {
            itsLogWriter.print(aString);
            itsLogWriter.flush();
        }
        else
        {
            LOG.info(aString);
        }
    }

    public static void println(String aString)
    {
        if(itsLogWriter != null)
        {
            itsLogWriter.println(aString);
        }
        else
        {
            LOG.info(aString);
        }
    }

    private static boolean isPrintableContentType(String aContentType)
    {
        if(aContentType == null)
        {
            return false;
        }

        for(String myPrintable : PRINTABLE_CONTENT_TYPES)
        {
            if(aContentType.toLowerCase().contains(myPrintable.toLowerCase()))
            {
                return true;
            }
        }

        return false;
    }

    public static BodyType printResponseHeaders(URLConnection aConnection)
    {
        Map<String, List<String>> myHeaders = aConnection.getHeaderFields();
        String myContentType = null;
        for(String myHeaderName : myHeaders.keySet())
        {
            for(String myHeaderValue : myHeaders.get(myHeaderName))
            {
                if(myHeaderName != null && myHeaderName.equalsIgnoreCase("Content-Type"))
                {
                    myContentType = myHeaderValue;
                }

                println((myHeaderName != null ? myHeaderName + ": " : "") + myHeaderValue);
            }
        }

        print("\n\n");

        return isPrintableContentType(myContentType) ? BodyType.printable : BodyType.notPrintable;
    }

    public static BodyType printRequestHeaders(URLConnection aConnection)
    {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");

        if(aConnection instanceof HttpURLConnection)
        {
            println(((HttpURLConnection)aConnection).getRequestMethod() + " " + aConnection.getURL().toString());
        }
        else
        {
            println(aConnection.getURL().toString());
        }


        Map<String, List<String>> myHeaders = aConnection.getRequestProperties();
        String myContentType = null;
        for(String myHeaderName : myHeaders.keySet())
        {
            for(String myHeaderValue : myHeaders.get(myHeaderName))
            {
                if(myHeaderName != null && myHeaderName.equalsIgnoreCase("Content-Type"))
                {
                    myContentType = myHeaderValue;
                }

                println((myHeaderName != null ? myHeaderName + ": " : "") + myHeaderValue);
            }
        }

        print("\n\n");

        return isPrintableContentType(myContentType) ? BodyType.printable : BodyType.notPrintable;
    }

}
