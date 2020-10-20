package com.eelink.tcp.model;

import java.util.Arrays;

public class EErx implements Cloneable {
	private byte[] m_data;
	private int m_size;
	private int m_error;
	private int m_index;

	public EErx(byte[] m_data, int m_size) {
		this.m_data = m_data;
		this.m_index = 0;
		this.m_size = m_size;
		this.m_error = 0;
	}

	public EErx(byte[] m_data, int m_index, int m_size) {
		this.m_data = m_data;
		this.m_index = m_index;
		this.m_size = m_size;
		this.m_error = 0;
	}

	public EErx(char[] m_data, int m_size) {
		this.m_data = new byte[m_size];
		for (int i = 0; i < m_size; i++) {
			this.m_data[i] = (byte) m_data[i];
		}
		this.m_size = m_size;
		this.m_error = 0;
		this.m_index = 0;
	}

	public byte[] getData() {
		return m_data;
	}

	public int getSize() {
		return m_size;
	}

	public int getIndex() {
		return m_index;
	}

	/**
	 * 解析 8 个字节（包含）以下有符号整数
	 * @param length
	 * @return
	 */
	public Number parseInt( int length )
	{
		long value = parseUInt( length ).longValue();
		int bits = length * 8;
		if ( ( value & ( 1L << ( bits - 1 ) ) ) > 0 )
			value = ~ ( value );

		return value;
	}

	/**
	 * 解析 8 个字节（不包含）以下无符号整数
	 * @param length
	 * @return
	 */
	public Number parseUInt( int length )
	{
		if ( length == 8 ) return 0;
		long value = 0;
		if ( this.m_error != 0 ) return value;
		if ( length > 8 || this.m_size < length ) return value;

		for ( int i = 0; i < length; i ++ )
		{
			value <<= 8;
			value += ( this.m_data[this.m_index ++] & 0xff );
		}
		this.m_size -= length;

		return value;
	}

	public byte[] parseData(int length) {
		if (m_error != 0)
			return null;

		if (m_size < length)
			return null;

		byte[] value = Arrays.copyOfRange(m_data, m_index, m_index + length);
		m_index += length;
		m_size -= length;
		return value;
	}

    public String parseIMEI() {
        if (m_size < 8)
            return "";
        String hex = "0123456789ABCDEF";
        char[] imei = new char[15];
        int imeiIndex = 0;
        imei[imeiIndex++] = hex.charAt( Byte.toUnsignedInt( m_data[m_index++] ) & 0x0f );
        for (int i = 0; i < 7; i++) {
            imei[imeiIndex++] = hex.charAt( Byte.toUnsignedInt( m_data[m_index] ) >> 4 );
            imei[imeiIndex++] = hex.charAt( Byte.toUnsignedInt( m_data[m_index++] ) & 0x0f );
        }
        m_size -= 8;
        return new String( imei );
    }
	
	public String parseHexStr( int length )
	{
		if (m_error != 0)
			return null;

		if (m_size < length)
			return null;
		
		StringBuilder strBuf = new StringBuilder( 2 * length );
		length += m_index;
		for ( ; m_index < length; m_index ++ )
		{
			strBuf.append( String.format( "%02x" , m_data[m_index] ) );
			m_size --;
		}
		
		return strBuf.toString().trim();
	}

	public void skip(int length) {
		length = Math.min(length, m_size);
		m_index += length;
		m_size -= length;
	}
	
	public void prev(int length) {
		length = Math.min(length, m_index);
		m_index -= length;
		m_size += length;
	}

	@Override
	public EErx clone() {
		EErx c = null;
		try {
			c = (EErx) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return c;
	}
	
}
