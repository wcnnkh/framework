package io.basc.framework.util.codec.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import io.basc.framework.io.BufferProcessor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.codec.CodecException;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.EncodeException;
import lombok.NonNull;

/**
 * 需要encodeKey和decoderKey的为非对称加解密
 * 
 * @author wcnnkh
 *
 */
public class CryptoCodec extends SecurityCodec implements Cloneable {
	private final CipherFactory encoder;
	private final CipherFactory decoder;

	public CryptoCodec(@NonNull String transformation, @NonNull Object key, Object params) {
		this(transformation, key, key, params);
	}

	public CryptoCodec(@NonNull String transformation, Object encoderKey, Object decoderKey, Object params) {
		this(encoderKey == null ? null : new CipherFactory(transformation, Cipher.ENCRYPT_MODE, encoderKey, params),
				decoderKey == null ? null : new CipherFactory(transformation, Cipher.DECRYPT_MODE, decoderKey, params));
	}

	public CryptoCodec(CipherFactory encoder, CipherFactory decoder) {
		this.encoder = encoder;
		this.decoder = decoder;
	}

	protected CryptoCodec(CryptoCodec codec) {
		this.decoder = codec.decoder;
		this.encoder = codec.encoder;
	}

	@Override
	public CryptoCodec clone() {
		return new CryptoCodec(this);
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

	public final CipherFactory getEncoder() {
		return encoder;
	}

	public final CipherFactory getDecoder() {
		return decoder;
	}

	@Override
	public byte[] encode(byte[] source) throws EncodeException {
		Assert.requiredArgument(encoder != null, "encoder");
		return encoder.doFinal(source);
	}

	@Override
	public byte[] decode(byte[] source) throws DecodeException {
		Assert.requiredArgument(decoder != null, "decoder");
		return decoder.doFinal(source);
	}

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		encode(source, bufferSize, target::write);
	}

	@Override
	public void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException {
		decode(source, bufferSize, target::write);
	}

	@Override
	public <E extends Throwable> void encode(InputStream source, int bufferSize,
			BufferProcessor<byte[], E> targetProcessor) throws IOException, EncodeException, E {
		Assert.requiredArgument(encoder != null, "encoder");
		encoder.doFinal(source, bufferSize, targetProcessor);
	}

	@Override
	public <E extends Throwable> void decode(InputStream source, int bufferSize,
			BufferProcessor<byte[], E> targetProcessor) throws DecodeException, IOException, E {
		Assert.requiredArgument(decoder != null, "decoder");
		decoder.doFinal(source, bufferSize, targetProcessor);
	}
}
