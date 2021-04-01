package scw.codec.encoder;

import scw.codec.Encoder;

/**
 * SHA-1签名算法
 * @author sha-1
 *
 */
public class SHA1 extends MessageDigestEncoder{
	public static final String ALGORITHM = "SHA-1";
	
	public static final Encoder<byte[], String> DEFAULT = new SHA1().toHex();
	
	public SHA1() {
		super(ALGORITHM);
	}

}
