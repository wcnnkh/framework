package scw.codec.support;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import scw.codec.CodecException;
import scw.codec.DecodeException;
import scw.codec.EncodeException;

/**
 * 对称编解码器
 * 
 * @author shuchaowen
 *
 */
public class SymmetricCodec extends CryptoCodec {
	protected final String algorithm;
	private final SecretKey secretKey;
	private final AlgorithmParameterSpec algorithmParameterSpec;

	public SymmetricCodec(String algorithm, SecretKey secretKey,
			AlgorithmParameterSpec algorithmParameterSpec) {
		this.algorithm = algorithm;
		this.secretKey = secretKey;
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	public Cipher getCipher() {
		return getCipher(algorithm);
	}

	public byte[] encode(byte[] source) throws EncodeException {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, algorithmParameterSpec);
		} catch (InvalidKeyException e) {
			throw new CodecException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}

		try {
			return cipher.doFinal(source);
		} catch (IllegalBlockSizeException e) {
			throw new EncodeException(e);
		} catch (BadPaddingException e) {
			throw new EncodeException(e);
		}
	}

	public byte[] decode(byte[] source) throws DecodeException {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey, algorithmParameterSpec);
		} catch (InvalidKeyException e) {
			throw new CodecException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}
		try {
			return cipher.doFinal(source);
		} catch (IllegalBlockSizeException e) {
			throw new DecodeException(e);
		} catch (BadPaddingException e) {
			throw new DecodeException(e);
		}
	}
}
