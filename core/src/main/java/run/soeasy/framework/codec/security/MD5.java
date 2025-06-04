package run.soeasy.framework.codec.security;

import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.codec.lang.HexCodec;

public class MD5 extends MessageDigestEncoder {
	public static final String ALGORITHM = "MD5";

	/**
	 * 默认实现将结果直接转换为16进制字符串
	 */
	public static final Encoder<byte[], String> DEFAULT = new MD5().toEncoder(HexCodec.DEFAULT);

	public MD5() {
		super(ALGORITHM);
	}

	protected MD5(MD5 md5) {
		super(md5);
	}

	@Override
	public MD5 clone() {
		return new MD5(this);
	}
}
