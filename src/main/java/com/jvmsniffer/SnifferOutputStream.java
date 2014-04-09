package com.jvmsniffer;

import java.io.IOException;
import java.io.OutputStream;


public class SnifferOutputStream extends OutputStream
{
    private OutputStream itsRealOutputStream;
    private BodyType itsBodyType;


    public SnifferOutputStream(OutputStream aRealOutputStream, BodyType aBodyType)
    {
        itsRealOutputStream = aRealOutputStream;
        itsBodyType = aBodyType;
    }

    @Override
    public void write(int b) throws IOException
    {
        if(itsBodyType == BodyType.printable)
        {
            JvmSniffer.print(new String(new byte[]{(byte)b}, "UTF-8"));
        }

        itsRealOutputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        if(itsBodyType == BodyType.printable)
        {
            JvmSniffer.print(new String(b, "UTF-8"));
        }

        itsRealOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        if(itsBodyType == BodyType.printable)
        {
            byte[] myWriteBytes = new byte[len];

            System.arraycopy(b, off, myWriteBytes, 0, len);

            JvmSniffer.print(new String(myWriteBytes, "UTF-8"));
        }

        itsRealOutputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException
    {
        itsRealOutputStream.flush();
    }

    @Override
    public void close() throws IOException
    {
        itsRealOutputStream.close();
        JvmSniffer.print("\n\n\n\n");
    }
}
