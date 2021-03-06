package scw.codec.support;

/**
 * SHA-1签名算法
 * @author sha-1
 *
 */
public class SHA1 extends MessageDigestSigner{
	public static final String ALGORITHM = "SHA-1";
	
	public SHA1() {
		super(ALGORITHM);
	}

}
