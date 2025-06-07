package run.soeasy.framework.codec.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.security.SecurityCodec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.io.BufferProcessor;

/**
 * 需要encodeKey和decoderKey的为非对称加解密
 * 
 * @author wcnnkh
 *
 */
public class CryptoCodec extends SecurityCodec {
	private final CipherFactory encoder;
	private final CipherFactory decoder;

	public CryptoCodec(@NonNull String transformation, Object provider, CipherInitializer encodeCipherInitializer,
			CipherInitializer decodeCipherInitializer) {
		this(encodeCipherInitializer == null ? null
				: new CipherFactory(transformation, provider, Cipher.ENCRYPT_MODE, encodeCipherInitializer),
				decodeCipherInitializer == null ? null
						: new CipherFactory(transformation, provider, Cipher.DECRYPT_MODE, decodeCipherInitializer));
	}

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

	public final CipherFactory getEncoder() {
		return encoder;
	}

	public final CipherFactory getDecoder() {
		return decoder;
	}

	@Override
	public boolean canEncode() {
		return encoder != null;
	}

	@Override
	public boolean canDecode() {
		return decoder != null;
	}

	@Override
	public byte[] encode(byte[] source) throws EncodeException {
		Assert.state(encoder != null, "encoder does not exist");
		try {
			return encoder.doFinal(source);
		} catch (GeneralSecurityException e) {
			throw new EncodeException(e);
		}
	}

	@Override
	public byte[] decode(byte[] source) throws DecodeException {
		Assert.state(decoder != null, "decoder does not exist");
		try {
			return decoder.doFinal(source);
		} catch (GeneralSecurityException e) {
			throw new DecodeException(e);
		}
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
		Assert.state(encoder != null, "encoder does not exist");
		try {
			encoder.doFinal(source, bufferSize, targetProcessor);
		} catch (IOException | GeneralSecurityException e) {
			throw new EncodeException(e);
		}
	}

	@Override
	public <E extends Throwable> void decode(InputStream source, int bufferSize,
			BufferProcessor<byte[], E> targetProcessor) throws DecodeException, IOException, E {
		Assert.state(decoder != null, "decoder does not exist");
		try {
			decoder.doFinal(source, bufferSize, targetProcessor);
		} catch (IOException | GeneralSecurityException e) {
			throw new DecodeException(e);
		}
	}
}
