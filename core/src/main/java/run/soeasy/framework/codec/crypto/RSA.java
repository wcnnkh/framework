package run.soeasy.framework.codec.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * RSA
 * 
 * @author wcnnkh
 *
 */
public class RSA extends AsymmetricCodec {
	public static final String RSA = "RSA";

	public RSA(PublicKey publicKey, PrivateKey privateKey, int maxBlock) {
		super(RSA, publicKey, privateKey, maxBlock);
	}

	public RSA(PrivateKey privateKey, PublicKey publicKey, int maxBlock) {
		super(RSA, privateKey, publicKey, maxBlock);
	}

	public static PrivateKey getPrivateKey(byte[] key) {
		return getPrivateKey(RSA, key);
	}

	public static PublicKey getPublicKey(byte[] key) {
		return getPublicKey(RSA, key);
	}
}
