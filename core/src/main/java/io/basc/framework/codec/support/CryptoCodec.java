package io.basc.framework.codec.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import io.basc.framework.codec.CodecException;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.BufferProcessor;
import io.basc.framework.io.IOUtils;
import io.basc.framework.util.Assert;

public class CryptoCodec extends SecurityCodec {
	private final CipherFactory encoder;
	private final CipherFactory decoder;

	public CryptoCodec(CipherFactory encoder, CipherFactory decoder) {
		this.encoder = encoder;
		this.decoder = decoder;
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

	public CipherFactory getEncoder() {
		return encoder;
	}

	public CipherFactory getDecoder() {
		return decoder;
	}

	@Override
	public byte[] encode(byte[] source) throws EncodeException {
		Cipher cipher;
		try {
			cipher = encoder.getCipher(Cipher.ENCRYPT_MODE);
		} catch (CodecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| NoSuchProviderException | InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}
		try {
			return cipher.doFinal(source);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new EncodeException(e);
		}
	}

	@Override
	public byte[] decode(byte[] source) throws DecodeException {
		Cipher cipher;
		try {
			cipher = encoder.getCipher(Cipher.DECRYPT_MODE);
		} catch (CodecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| NoSuchProviderException | InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}
		try {
			return cipher.doFinal(source);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new EncodeException(e);
		}
	}

	public <E extends Throwable> void doFinal(int opmode, int maxBlock, InputStream source,
			BufferProcessor<byte[], E> targetProcessor) throws Throwable {
		Assert.requiredArgument(source != null, "source");
		Cipher cipher = get
		IOUtils.read(source, maxBlock, (buff, offset, len) -> {
			byte[] target = cipher.doFinal(buff, offset, len);
			targetProcessor.process(target, 0, target.length);
		});
	}

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		Cipher cipher;
		try {
			cipher = encoder.getCipher(Cipher.DECRYPT_MODE);
		} catch (CodecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| NoSuchProviderException | InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}
		try {
			return cipher.doFinal(source);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new EncodeException(e);
		}
	}

	@Override
	public void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException {
		Cipher cipher;
		try {
			cipher = encoder.getCipher(Cipher.DECRYPT_MODE);
		} catch (CodecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| NoSuchProviderException | InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}
		try {
			return cipher.doFinal(source);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new EncodeException(e);
		}
	}
}
