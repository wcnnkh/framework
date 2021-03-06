package scw.codec.support;

import scw.codec.AbstractCodec;
import scw.codec.DecodeException;
import scw.codec.EncodeException;

/**
 * 2进制与16进制的转换
 * @author asus1
 *
 */
public class ByteHexCodec extends AbstractCodec<byte[], String> {

	public String encode(byte[] source) throws EncodeException {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < source.length; i++) {
			String hex = Integer.toHexString(source[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex);
		}
		return sign.toString();
	}

	/**
	 * 两个16进制字符表示一个字节长度<br/>
	 * 因为一个字节等于8bit（bit = 二进制位）<br/>
	 * 一个16进制字符 需要 4个二进制才能表示<br/>
	 * 所以两个16进制字符等于一个字节<br/>
	 */
	public byte[] decode(String source) throws DecodeException {
		if (source.length() < 1)
			return null;
		byte[] result = new byte[source.length() / 2];
		for (int i = 0; i < source.length() / 2; i++) {
			int high = Integer.parseInt(source.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(source.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

}
