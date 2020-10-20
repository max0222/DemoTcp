package com.eelink.tcp.model;

public class EEtx {
	private static final int BUF_SIZE = 2000;

	private byte[] m_data;

	private int m_size;

	private int m_capacity;

	public EEtx() {
		m_capacity = BUF_SIZE;
		reset();
	}

	private void reset() {
		m_data = new byte[m_capacity];
		m_size = 0;
	}

	public byte[] getData() {
		return m_data;
	}

	public int getSize() {
		return m_size;
	}

	public void writeInt(int value, int len) {
		writeLong(value, len);
	}

	public void writeLong(long value, int len) {
		final int _shift[] = { 0, 8, 16, 24, 32, 40, 48, 56, 64 };
		for (int i = 0; i < len; i++) {
			m_data[m_size++] = (byte) (value >> _shift[len - 1 - i]);
		}
	}

	public void writeData(byte[] values, int pos, int length) {
		if (0 == length)
			return;
		for (int i = 0; i < length; i++) {
			m_data[m_size++] = values[pos + i];
		}
	}

	public void finalize() {
		m_data[3] = (byte) (((m_size - 5) >> 8) & 0xFF);
		m_data[4] = (byte) (((m_size - 5) >> 0) & 0xFF);
	}

	public void sprintf(final String format, Object... args) {
		byte[] bytes =  String.format(format, args).getBytes();
		System.arraycopy(bytes, 0, m_data, m_size, bytes.length);
		m_size += bytes.length;
	}
}
