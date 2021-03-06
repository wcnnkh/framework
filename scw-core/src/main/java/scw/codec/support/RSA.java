package scw.codec.support;

import java.security.Key;


/**
 * RSA
 * @author shuchaowen
 *
 */
public class RSA extends AsymmetricCodec {
	/**
	 * ALGORITHM ['ælgərɪð(ə)m] 算法的意思
	 */
	public static final String RSA_ALGORITHM = "RSA";
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	public RSA(Key key) {
		super(RSA_ALGORITHM, SIGN_ALGORITHMS, key);
	}
	
	public static RSA getPrivateKeyCodec(byte[] privateKey){
		return new RSA(getPrivateKey(RSA_ALGORITHM, privateKey));
	}
	
	public static RSA getPublicKeyCodec(byte[] publicKey){
		return new RSA(getPublicKey(RSA_ALGORITHM, publicKey));
	}
}
