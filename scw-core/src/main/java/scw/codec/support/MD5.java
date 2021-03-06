package scw.codec.support;

public class MD5 extends MessageDigestSigner {
	public static final String ALGORITHM = "MD5";

	public MD5() {
		super(ALGORITHM);
	}

}
