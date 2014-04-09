package com.jvmsniffer;

import java.io.IOException;
import java.io.InputStream;


public class SnifferInputStream extends InputStream
{
    private InputStream itsRealInputStream;
    private BodyType itsBodyType;


    /**
     * The object used to synchronize access to the reader.
     */
    protected final Object itsLock;


    public SnifferInputStream(InputStream aRealInputStream, BodyType aBodyType)
    {
        itsLock = this;
        itsRealInputStream = aRealInputStream;
        itsBodyType = aBodyType;
    }


    @Override
    public int read() throws IOException
    {
        synchronized (itsLock)
        {
            byte myByte = (byte) itsRealInputStream.read();

            if(myByte > 0 && itsBodyType == BodyType.printable)
            {
                JvmSniffer.print(new String(new byte[]{myByte}, "UTF-8"));
            }
            else if(itsBodyType == BodyType.printable)
            {
                JvmSniffer.print("\n\n\n");
            }

            return myByte;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        synchronized (itsLock)
        {
            int myResult = itsRealInputStream.read(b, off, len);

            if(myResult > 0 && itsBodyType == BodyType.printable)
            {
                byte[] myReadBytes = new byte[myResult];

                System.arraycopy(b, off, myReadBytes, 0, myResult);
                JvmSniffer.print(new String(myReadBytes, "UTF-8"));
            }
            else if(itsBodyType == BodyType.printable)
            {
                JvmSniffer.print("\n\n\n");
            }

            return myResult;
        }
    }

    @Override
    public long skip(long n) throws IOException
    {
        return itsRealInputStream.skip(n);
    }

    @Override
    public int available() throws IOException
    {
        return itsRealInputStream.available();
    }

    @Override
    public void close() throws IOException
    {
        itsRealInputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit)
    {
        itsRealInputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException
    {
        itsRealInputStream.reset();
    }

    @Override
    public boolean markSupported()
    {
        return itsRealInputStream.markSupported();
    }
}
