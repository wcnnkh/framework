package run.soeasy.framework.core.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 取自jdk的实现 Utility methods for packing/unpacking primitive values in/out of
 * byte arrays using big-endian byte ordering.
 * 
 * @author soeasy.run
 *
 */
public final class Bits {
	private Bits() {
	};

	/*
	 * Methods for unpacking primitive values from byte arrays starting at given
	 * offsets.
	 */
	public static boolean getBoolean(byte[] b, int off) {
		return b[off] != 0;
	}

	public static boolean readBoolean(InputStream source) throws IOException {
		int value = source.read();
		if (value == -1) {
			throw new EOFException();
		}
		return value != 0;
	}

	public static char getChar(byte[] b, int off) {
		return (char) ((b[off + 1] & 0xFF) + (b[off] << 8));
	}

	public static short getShort(byte[] b, int off) {
		return (short) ((b[off + 1] & 0xFF) + (b[off] << 8));
	}

	public static short readShort(InputStream source) throws EOFException, IOException {
		byte[] buff = new byte[2];
		int size = source.read(buff);
		if (size != buff.length) {
			throw new EOFException("read buff=" + Arrays.toString(buff) + ", size=" + size);
		}
		return getShort(buff, 0);
	}

	public static int getInt(byte[] b, int off) {
		return ((b[off + 3] & 0xFF)) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16) + ((b[off]) << 24);
	}

	public static int readInt(InputStream source) throws IOException {
		byte[] buff = new byte[4];
		int size = source.read(buff);
		if (size != buff.length) {
			throw new EOFException("read buff=" + Arrays.toString(buff) + ", size=" + size);
		}
		return getInt(buff, 0);
	}

	public static float getFloat(byte[] b, int off) {
		return Float.intBitsToFloat(getInt(b, off));
	}

	public static long getLong(byte[] b, int off) {
		return ((b[off + 7] & 0xFFL)) + ((b[off + 6] & 0xFFL) << 8) + ((b[off + 5] & 0xFFL) << 16)
				+ ((b[off + 4] & 0xFFL) << 24) + ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40)
				+ ((b[off + 1] & 0xFFL) << 48) + (((long) b[off]) << 56);
	}

	public static long readLong(InputStream source) throws IOException {
		byte[] buff = new byte[8];
		int size = source.read(buff);
		if (size != buff.length) {
			throw new EOFException("read buff=" + Arrays.toString(buff) + ", size=" + size);
		}
		return getLong(buff, 0);
	}

	public static double getDouble(byte[] b, int off) {
		return Double.longBitsToDouble(getLong(b, off));
	}

	/*
	 * Methods for packing primitive values into byte arrays starting at given
	 * offsets.
	 */

	public static void putBoolean(byte[] b, int off, boolean val) {
		b[off] = (byte) (val ? 1 : 0);
	}

	public static void writeBoolean(boolean source, OutputStream target) throws IOException {
		target.write(source ? 1 : 0);
	}

	/**
	 * Java语言规范规定，Java的char类型是UTF-16的code unit，也就是一定是16位（2字节）
	 * 
	 * @param b
	 * @param off
	 * @param val
	 */
	public static void putChar(byte[] b, int off, char val) {
		b[off + 1] = (byte) (val);
		b[off] = (byte) (val >>> 8);
	}

	/**
	 * 占用两个字节
	 * 
	 * @param b
	 * @param off
	 * @param val
	 */
	public static void putShort(byte[] b, int off, short val) {
		b[off + 1] = (byte) (val);
		b[off] = (byte) (val >>> 8);
	}

	public static void writeShort(short source, OutputStream target) throws IOException {
		byte[] buff = new byte[2];
		putShort(buff, 0, source);
		target.write(buff);
	}

	/**
	 * 一个int占用4个字节
	 * 
	 * @param b
	 * @param off
	 * @param val
	 */
	public static void putInt(byte[] b, int off, int val) {
		b[off + 3] = (byte) (val);
		b[off + 2] = (byte) (val >>> 8);
		b[off + 1] = (byte) (val >>> 16);
		b[off] = (byte) (val >>> 24);
	}

	public static void writeInt(int source, OutputStream target) throws IOException {
		byte[] buff = new byte[4];
		putInt(buff, 0, source);
		target.write(buff);
	}

	/**
	 * 占用4个字节
	 * 
	 * @param b
	 * @param off
	 * @param val
	 */
	public static void putFloat(byte[] b, int off, float val) {
		putInt(b, off, Float.floatToIntBits(val));
	}

	/**
	 * 占用8个字节
	 * 
	 * @param b
	 * @param off
	 * @param val
	 */
	public static void putLong(byte[] b, int off, long val) {
		b[off + 7] = (byte) (val);
		b[off + 6] = (byte) (val >>> 8);
		b[off + 5] = (byte) (val >>> 16);
		b[off + 4] = (byte) (val >>> 24);
		b[off + 3] = (byte) (val >>> 32);
		b[off + 2] = (byte) (val >>> 40);
		b[off + 1] = (byte) (val >>> 48);
		b[off] = (byte) (val >>> 56);
	}

	public static void writeLong(long source, OutputStream target) throws IOException {
		byte[] buff = new byte[8];
		putLong(buff, 0, source);
		target.write(buff);
	}

	/**
	 * 占用8个字节
	 * 
	 * @param b
	 * @param off
	 * @param val
	 */
	public static void putDouble(byte[] b, int off, double val) {
		putLong(b, off, Double.doubleToLongBits(val));
	}
}
