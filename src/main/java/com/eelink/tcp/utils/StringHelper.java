package com.eelink.tcp.utils;

public class StringHelper {

    public static String TO_STRING( byte[] bytes )
    {
        return bytes == null ? "00" : TO_STRING( bytes, 0, bytes.length );
    }

    public static String TO_STRING( byte[] bytes, int index, int size )
    {
        int length = index + size;
        if ( bytes == null || bytes.length < length ) return "00";

        StringBuilder strBuf = new StringBuilder( 2 * bytes.length );
        for ( ; index < length; index ++ )
        {
            strBuf.append( String.format( "%02x", bytes[ index ] ) );
        }

        return strBuf.toString().trim();
    }

}
