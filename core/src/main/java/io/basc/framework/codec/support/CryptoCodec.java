package io.basc.framework.codec.support;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import io.basc.framework.codec.CodecException;

public abstract class CryptoCodec extends SecurityCodec {

	public static Cipher getCipher(String algorithm) throws CodecException {
		try {
			return Cipher.getInstance(algorithm);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new CodecException(algorithm, e);
		}
	}

	public static SecretKeyFactory getSecretKeyFactory(String algorithm) {
		try {
			return SecretKeyFactory.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CodecException(algorithm, e);
		}
	}

	public static SecretKey getSecretKey(String algorithm, KeySpec keySpec) {
		SecretKeyFactory keyFactory = getSecretKeyFactory(algorithm);
		try {
			return keyFactory.generateSecret(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CodecException(e);
		}
	}

	public static SecretKey getSecretKey(String algorithm, byte[] secretKey) {
		return new SecretKeySpec(secretKey, algorithm);
	}
}
