package io.basc.framework.codec.encode;


public class HmacSHA1 extends MAC {
	public static final String ALGORITHM = "HmacSHA1";

	public HmacSHA1(byte[] secretKey) {
		super(ALGORITHM, secretKey);
	}
}
