package io.basc.framework.codec.encode;

import io.basc.framework.codec.Encoder;

/**
 * SHA-1签名算法
 * 
 * @author sha-1
 *
 */
public class SHA1 extends MessageDigestEncoder {
	public static final String ALGORITHM = "SHA-1";

	public static final Encoder<byte[], String> DEFAULT = new SHA1().toHex();

	public SHA1() {
		super(ALGORITHM);
	}

	protected SHA1(SHA1 sha1) {
		super(sha1);
	}

	@Override
	public SHA1 clone() {
		return new SHA1(this);
	}
}
