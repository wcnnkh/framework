package scw.codec.support;

import scw.codec.Signer;

/**
 * SHA-1签名算法
 * @author sha-1
 *
 */
public class SHA1 extends MessageDigestSigner{
	public static final String ALGORITHM = "SHA-1";
	
	public static final Signer<byte[], String> DEFAULT = new SHA1().toHex();
	
	public SHA1() {
		super(ALGORITHM);
	}

}
