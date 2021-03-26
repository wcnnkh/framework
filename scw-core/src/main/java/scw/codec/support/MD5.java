package scw.codec.support;

import scw.codec.Signer;


public class MD5 extends MessageDigestSigner {
	public static final String ALGORITHM = "MD5";
	
	public static final Signer<byte[], String> DEFAULT = new MD5().toHex();

	public MD5() {
		super(ALGORITHM);
	}

}
