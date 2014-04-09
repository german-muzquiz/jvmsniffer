package com.jvmsniffer.connections;

import com.jvmsniffer.BodyType;
import com.jvmsniffer.JvmSniffer;
import com.jvmsniffer.SnifferInputStream;
import com.jvmsniffer.SnifferOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Permission;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;


public class SnifferHttpsURLConnection extends HttpsURLConnection
{
    private HttpsURLConnection itsRealConnection;
    private byte[] itsErrorResponse;
    private BodyType itsOutputType;


    public SnifferHttpsURLConnection(URL url, HttpsURLConnection aRealConnection)
    {
        super(url);
        itsRealConnection = aRealConnection;
    }


    @Override
    public void connect() throws IOException
    {
        itsRealConnection.connect();
    }

    @Override
    public void setConnectTimeout(int timeout)
    {
        itsRealConnection.setConnectTimeout(timeout);
    }

    @Override
    public int getConnectTimeout()
    {
        return itsRealConnection.getConnectTimeout();
    }

    @Override
    public void setReadTimeout(int timeout)
    {
        itsRealConnection.setReadTimeout(timeout);
    }

    @Override
    public int getReadTimeout()
    {
        return itsRealConnection.getReadTimeout();
    }

    @Override
    public URL getURL()
    {
        return itsRealConnection.getURL();
    }

    @Override
    public int getContentLength()
    {
        return itsRealConnection.getContentLength();
    }

    @Override
    public long getContentLengthLong()
    {
        return itsRealConnection.getContentLengthLong();
    }

    @Override
    public String getContentType()
    {
        return itsRealConnection.getContentType();
    }

    @Override
    public String getContentEncoding()
    {
        return itsRealConnection.getContentEncoding();
    }

    @Override
    public long getExpiration()
    {
        return itsRealConnection.getExpiration();
    }

    @Override
    public long getDate()
    {
        return itsRealConnection.getDate();
    }

    @Override
    public long getLastModified()
    {
        return itsRealConnection.getLastModified();
    }

    @Override
    public String getHeaderField(String name)
    {
        return itsRealConnection.getHeaderField(name);
    }

    @Override
    public Map<String, List<String>> getHeaderFields()
    {
        return itsRealConnection.getHeaderFields();
    }

    @Override
    public int getHeaderFieldInt(String name, int Default)
    {
        return itsRealConnection.getHeaderFieldInt(name, Default);
    }

    @Override
    public long getHeaderFieldLong(String name, long Default)
    {
        return itsRealConnection.getHeaderFieldLong(name, Default);
    }

    @Override
    public long getHeaderFieldDate(String name, long Default)
    {
        return itsRealConnection.getHeaderFieldDate(name, Default);
    }

    @Override
    public String getHeaderFieldKey(int n)
    {
        return itsRealConnection.getHeaderFieldKey(n);
    }

    @Override
    public String getHeaderField(int n)
    {
        return itsRealConnection.getHeaderField(n);
    }

    @Override
    public Object getContent() throws IOException
    {
        return itsRealConnection.getContent();
    }

    @Override
    public Object getContent(Class[] classes) throws IOException
    {
        return itsRealConnection.getContent(classes);
    }

    @Override
    public Permission getPermission() throws IOException
    {
        return itsRealConnection.getPermission();
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        try
        {
            if(itsOutputType == null)
            {
                itsOutputType = JvmSniffer.printRequestHeaders(itsRealConnection);
            }

            BodyType myBodyType = JvmSniffer.printResponseHeaders(itsRealConnection);
            return new SnifferInputStream(itsRealConnection.getInputStream(), myBodyType);
        }
        catch(IOException anEx)
        {
            DataInputStream in = new DataInputStream(getErrorStream());
            itsErrorResponse = new byte[2048];

            try
            {
                // Reading from the stream actually logs all the data
                in.readFully(itsErrorResponse);
            }
            catch(EOFException eof)
            {
                // ignore
            }

            throw anEx;
        }
    }

    @Override
    public InputStream getErrorStream()
    {
        if(itsErrorResponse != null)
        {
            return new ByteArrayInputStream(itsErrorResponse);
        }
        else
        {
            return new SnifferInputStream(itsRealConnection.getErrorStream(), BodyType.printable);
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        if(itsOutputType == null)
        {
            itsOutputType = JvmSniffer.printRequestHeaders(itsRealConnection);
        }

        return new SnifferOutputStream(itsRealConnection.getOutputStream(), itsOutputType);
    }

    @Override
    public String toString()
    {
        return itsRealConnection.toString();
    }

    @Override
    public void setDoInput(boolean doinput)
    {
        itsRealConnection.setDoInput(doinput);
    }

    @Override
    public boolean getDoInput()
    {
        return itsRealConnection.getDoInput();
    }

    @Override
    public void setDoOutput(boolean dooutput)
    {
        itsRealConnection.setDoOutput(dooutput);
    }

    @Override
    public boolean getDoOutput()
    {
        return itsRealConnection.getDoOutput();
    }

    @Override
    public void setAllowUserInteraction(boolean allowuserinteraction)
    {
        itsRealConnection.setAllowUserInteraction(allowuserinteraction);
    }

    @Override
    public boolean getAllowUserInteraction()
    {
        return itsRealConnection.getAllowUserInteraction();
    }

    @Override
    public void setUseCaches(boolean usecaches)
    {
        itsRealConnection.setUseCaches(usecaches);
    }

    @Override
    public boolean getUseCaches()
    {
        return itsRealConnection.getUseCaches();
    }

    @Override
    public void setIfModifiedSince(long ifmodifiedsince)
    {
        itsRealConnection.setIfModifiedSince(ifmodifiedsince);
    }

    @Override
    public long getIfModifiedSince()
    {
        return itsRealConnection.getIfModifiedSince();
    }

    @Override
    public boolean getDefaultUseCaches()
    {
        return itsRealConnection.getDefaultUseCaches();
    }

    @Override
    public void setDefaultUseCaches(boolean defaultusecaches)
    {
        itsRealConnection.setDefaultUseCaches(defaultusecaches);
    }

    @Override
    public void setRequestProperty(String key, String value)
    {
        itsRealConnection.setRequestProperty(key, value);
    }

    @Override
    public void addRequestProperty(String key, String value)
    {
        itsRealConnection.addRequestProperty(key, value);
    }

    @Override
    public String getRequestProperty(String key)
    {
        return itsRealConnection.getRequestProperty(key);
    }

    @Override
    public Map<String, List<String>> getRequestProperties()
    {
        return itsRealConnection.getRequestProperties();
    }

    @Override
    public void setFixedLengthStreamingMode(int contentLength)
    {
        itsRealConnection.setFixedLengthStreamingMode(contentLength);
    }

    @Override
    public void setFixedLengthStreamingMode(long contentLength)
    {
        itsRealConnection.setFixedLengthStreamingMode(contentLength);
    }

    @Override
    public void setChunkedStreamingMode(int chunklen)
    {
        itsRealConnection.setChunkedStreamingMode(chunklen);
    }

    @Override
    public void setInstanceFollowRedirects(boolean followRedirects)
    {
        itsRealConnection.setInstanceFollowRedirects(followRedirects);
    }

    @Override
    public boolean getInstanceFollowRedirects()
    {
        return itsRealConnection.getInstanceFollowRedirects();
    }

    @Override
    public void setRequestMethod(String method) throws ProtocolException
    {
        itsRealConnection.setRequestMethod(method);
    }

    @Override
    public String getRequestMethod()
    {
        return itsRealConnection.getRequestMethod();
    }

    @Override
    public int getResponseCode() throws IOException
    {
        return itsRealConnection.getResponseCode();
    }

    @Override
    public String getResponseMessage() throws IOException
    {
        return itsRealConnection.getResponseMessage();
    }

    @Override
    public void disconnect()
    {
        itsRealConnection.disconnect();
    }

    @Override
    public boolean usingProxy()
    {
        return itsRealConnection.usingProxy();
    }

    @Override
    public String getCipherSuite()
    {
        return itsRealConnection.getCipherSuite();
    }

    @Override
    public Certificate[] getLocalCertificates()
    {
        return itsRealConnection.getLocalCertificates();
    }

    @Override
    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException
    {
        return itsRealConnection.getServerCertificates();
    }

    @Override
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException
    {
        return itsRealConnection.getPeerPrincipal();
    }

    @Override
    public Principal getLocalPrincipal()
    {
        return itsRealConnection.getLocalPrincipal();
    }

    @Override
    public void setHostnameVerifier(HostnameVerifier hostnameVerifier)
    {
        itsRealConnection.setHostnameVerifier(hostnameVerifier);
    }

    @Override
    public HostnameVerifier getHostnameVerifier()
    {
        return itsRealConnection.getHostnameVerifier();
    }

    @Override
    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory)
    {
        itsRealConnection.setSSLSocketFactory(sslSocketFactory);
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory()
    {
        return itsRealConnection.getSSLSocketFactory();
    }
}
