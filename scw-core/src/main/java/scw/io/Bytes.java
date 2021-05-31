package scw.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.core.Assert;

public final class Bytes {
	private Bytes() {
	};

	/**
	 * byte array copy.
	 * 
	 * @param src    src.
	 * @param length new length.
	 * @return new byte array.
	 */
	public static byte[] copyOf(byte[] src, int length) {
		byte[] dest = new byte[length];
		System.arraycopy(src, 0, dest, 0, Math.min(src.length, length));
		return dest;
	}

	public static List<byte[]> string2byteList(Collection<String> strings) {
		if (strings == null) {
			return null;
		}

		List<byte[]> list = new ArrayList<byte[]>(strings.size());
		Iterator<String> iterator = strings.iterator();
		while (iterator.hasNext()) {
			String v = iterator.next();
			if (v == null) {
				list.add(null);
				continue;
			}

			list.add(string2bytes(v));
		}
		return list;
	}

	public static byte[][] string2bytes(Collection<String> strings) {
		if (strings == null) {
			return null;
		}

		byte[][] bs = new byte[strings.size()][];
		int i = 0;
		Iterator<String> iterator = strings.iterator();
		while (iterator.hasNext()) {
			String v = iterator.next();
			bs[i++] = string2bytes(v);
		}
		return bs;
	}

	public static byte[] string2bytes(String string) {
		if (string == null) {
			return null;
		}

		int size = string.length();
		byte[] bytes = new byte[size * 2];
		for (int i = 0, b = 0; i < size; i++, b += 2) {
			Bits.putChar(bytes, b, string.charAt(i));
		}
		return bytes;
	}

	public static String bytes2String(byte[] b) {
		if (b == null) {
			return null;
		}

		return new String(bytes2chars(b));
	}

	public static byte[][] string2bytes(String... strings) {
		if (strings == null) {
			return null;
		}

		byte[][] bs = new byte[strings.length][];
		for (int i = 0; i < strings.length; i++) {
			bs[i] = string2bytes(strings[i]);
		}
		return bs;
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @return byte[].
	 */
	public static byte[] short2bytes(short v) {
		byte[] ret = { 0, 0 };
		short2bytes(v, ret);
		return ret;
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @param b byte array.
	 */
	public static void short2bytes(short v, byte[] b) {
		short2bytes(v, b, 0);
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @param b byte array.
	 */
	public static void short2bytes(short v, byte[] b, int off) {
		b[off + 1] = (byte) v;
		b[off + 0] = (byte) (v >>> 8);
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @return byte[].
	 */
	public static byte[] int2bytes(int v) {
		byte[] ret = { 0, 0, 0, 0 };
		int2bytes(v, ret);
		return ret;
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @param b byte array.
	 */
	public static void int2bytes(int v, byte[] b) {
		int2bytes(v, b, 0);
	}

	/**
	 * to byte array.
	 * 
	 * @param v   value.
	 * @param b   byte array.
	 * @param off array offset.
	 */
	public static void int2bytes(int v, byte[] b, int off) {
		b[off + 3] = (byte) v;
		b[off + 2] = (byte) (v >>> 8);
		b[off + 1] = (byte) (v >>> 16);
		b[off + 0] = (byte) (v >>> 24);
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @return byte[].
	 */
	public static byte[] float2bytes(float v) {
		byte[] ret = { 0, 0, 0, 0 };
		float2bytes(v, ret);
		return ret;
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @param b byte array.
	 */
	public static void float2bytes(float v, byte[] b) {
		float2bytes(v, b, 0);
	}

	/**
	 * to byte array.
	 * 
	 * @param v   value.
	 * @param b   byte array.
	 * @param off array offset.
	 */
	public static void float2bytes(float v, byte[] b, int off) {
		int i = Float.floatToIntBits(v);
		b[off + 3] = (byte) i;
		b[off + 2] = (byte) (i >>> 8);
		b[off + 1] = (byte) (i >>> 16);
		b[off + 0] = (byte) (i >>> 24);
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @return byte[].
	 */
	public static byte[] long2bytes(long v) {
		byte[] ret = { 0, 0, 0, 0, 0, 0, 0, 0 };
		long2bytes(v, ret);
		return ret;
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @param b byte array.
	 */
	public static void long2bytes(long v, byte[] b) {
		long2bytes(v, b, 0);
	}

	/**
	 * to byte array.
	 * 
	 * @param v   value.
	 * @param b   byte array.
	 * @param off array offset.
	 */
	public static void long2bytes(long v, byte[] b, int off) {
		b[off + 7] = (byte) v;
		b[off + 6] = (byte) (v >>> 8);
		b[off + 5] = (byte) (v >>> 16);
		b[off + 4] = (byte) (v >>> 24);
		b[off + 3] = (byte) (v >>> 32);
		b[off + 2] = (byte) (v >>> 40);
		b[off + 1] = (byte) (v >>> 48);
		b[off + 0] = (byte) (v >>> 56);
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @return byte[].
	 */
	public static byte[] double2bytes(double v) {
		byte[] ret = { 0, 0, 0, 0, 0, 0, 0, 0 };
		double2bytes(v, ret);
		return ret;
	}

	/**
	 * to byte array.
	 * 
	 * @param v value.
	 * @param b byte array.
	 */
	public static void double2bytes(double v, byte[] b) {
		double2bytes(v, b, 0);
	}

	/**
	 * to byte array.
	 * 
	 * @param v   value.
	 * @param b   byte array.
	 * @param off array offset.
	 */
	public static void double2bytes(double v, byte[] b, int off) {
		long j = Double.doubleToLongBits(v);
		b[off + 7] = (byte) j;
		b[off + 6] = (byte) (j >>> 8);
		b[off + 5] = (byte) (j >>> 16);
		b[off + 4] = (byte) (j >>> 24);
		b[off + 3] = (byte) (j >>> 32);
		b[off + 2] = (byte) (j >>> 40);
		b[off + 1] = (byte) (j >>> 48);
		b[off + 0] = (byte) (j >>> 56);
	}

	/**
	 * to short.
	 * 
	 * @param b byte array.
	 * @return short.
	 */
	public static short bytes2short(byte[] b) {
		return bytes2short(b, 0);
	}

	/**
	 * to short.
	 * 
	 * @param b   byte array.
	 * @param off offset.
	 * @return short.
	 */
	public static short bytes2short(byte[] b, int off) {
		return (short) (((b[off + 1] & 0xFF) << 0) + ((b[off + 0]) << 8));
	}

	/**
	 * to int.
	 * 
	 * @param b byte array.
	 * @return int.
	 */
	public static int bytes2int(byte[] b) {
		return bytes2int(b, 0);
	}

	/**
	 * to int.
	 * 
	 * @param b   byte array.
	 * @param off offset.
	 * @return int.
	 */
	public static int bytes2int(byte[] b, int off) {
		return ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16)
				+ ((b[off + 0]) << 24);
	}

	/**
	 * to int.
	 * 
	 * @param b byte array.
	 * @return int.
	 */
	public static float bytes2float(byte[] b) {
		return bytes2float(b, 0);
	}

	/**
	 * to int.
	 * 
	 * @param b   byte array.
	 * @param off offset.
	 * @return int.
	 */
	public static float bytes2float(byte[] b, int off) {
		int i = ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16)
				+ ((b[off + 0]) << 24);
		return Float.intBitsToFloat(i);
	}

	/**
	 * to long.
	 * 
	 * @param b byte array.
	 * @return long.
	 */
	public static long bytes2long(byte[] b) {
		return bytes2long(b, 0);
	}

	/**
	 * to long.
	 * 
	 * @param b   byte array.
	 * @param off offset.
	 * @return long.
	 */
	public static long bytes2long(byte[] b, int off) {
		return ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8) + ((b[off + 5] & 0xFFL) << 16)
				+ ((b[off + 4] & 0xFFL) << 24) + ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40)
				+ ((b[off + 1] & 0xFFL) << 48) + (((long) b[off + 0]) << 56);
	}

	/**
	 * to long.
	 * 
	 * @param b byte array.
	 * @return double.
	 */
	public static double bytes2double(byte[] b) {
		return bytes2double(b, 0);
	}

	/**
	 * to long.
	 * 
	 * @param b   byte array.
	 * @param off offset.
	 * @return double.
	 */
	public static double bytes2double(byte[] b, int off) {
		long j = ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8) + ((b[off + 5] & 0xFFL) << 16)
				+ ((b[off + 4] & 0xFFL) << 24) + ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40)
				+ ((b[off + 1] & 0xFFL) << 48) + (((long) b[off + 0]) << 56);
		return Double.longBitsToDouble(j);
	}

	public static char[] bytes2chars(byte[] b) {
		if ((b.length & 1) != 0) {
			throw new IllegalArgumentException("一个char占用两个byte，所以输入的byte数组长度必须是一个偶数");
		}

		char[] chars = new char[b.length >> 1];
		for (int i = 0, index = 0; i < b.length; i += 2, index++) {
			chars[index] = Bits.getChar(b, i);
		}
		return chars;
	}
	
	/**
	 * Extract a byte array from {@link ByteBuffer} without consuming it.
	 *
	 * @param byteBuffer must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	public static byte[] getBytes(ByteBuffer byteBuffer) {
		Assert.notNull(byteBuffer, "ByteBuffer must not be null!");
		ByteBuffer duplicate = byteBuffer.duplicate();
		byte[] bytes = new byte[duplicate.remaining()];
		duplicate.get(bytes);
		return bytes;
	}
}
