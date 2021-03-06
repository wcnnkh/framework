package scw.codec.support;

import java.security.InvalidKeyException;

import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import scw.codec.CodecException;

/**
 * DES
 * 
 * @author shuchaowen
 *
 */
public class DES extends SymmetricCodec {
	public static final String ALGORITHM = "DES/CBC/PKCS5Padding";
	public static final String KEY_ALGORITHM = "DES";

	public DES(byte[] secretKey) {
		super(ALGORITHM, getSecretKey(secretKey),
				new IvParameterSpec(secretKey));
	}

	public static SecretKey getSecretKey(byte[] secretKey) {
		DESKeySpec desKeySpec;
		try {
			desKeySpec = new DESKeySpec(secretKey);
		} catch (InvalidKeyException e) {
			throw new CodecException(e);
		}
		return getSecretKey(KEY_ALGORITHM, desKeySpec);
	}
}
