package io.basc.framework.util.codec.support;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.codec.CodecException;
import io.basc.framework.util.io.BufferProcessor;
import io.basc.framework.util.io.IOUtils;
import io.basc.framework.util.reflect.ReflectionUtils;
import lombok.NonNull;

public class CipherFactory implements Cloneable {
	private final ThreadLocal<Cipher> threadLocal;
	private final int opmode;
	private final String transformation;
	private final Object key;
	private final Object params;
	private final SecureRandom secureRandom;
	private final Object provider;

	public CipherFactory(@NonNull String transformation, int opmode, @NonNull Object key, Object params) {
		this(transformation, null, opmode, key, params, null);
	}

	public CipherFactory(@NonNull String transformation, Object provider, int opmode, @NonNull Object key,
			Object params) {
		this(transformation, provider, opmode, key, params, null);
	}

	public CipherFactory(@NonNull String transformation, Object provider, int opmode, @NonNull Object key,
			Object params, SecureRandom secureRandom) {
		Assert.requiredArgument(StringUtils.hasText(transformation), "transformation");
		Assert.requiredArgument(key != null, "key");
		this.threadLocal = new NamedThreadLocal<Cipher>(transformation);
		this.transformation = transformation;
		this.provider = provider;
		this.opmode = opmode;
		this.key = key;
		this.params = params;
		this.secureRandom = secureRandom;
	}

	protected CipherFactory(CipherFactory cipherFactory) {
		this.threadLocal = cipherFactory.threadLocal;
		this.transformation = cipherFactory.transformation;
		this.provider = cipherFactory.provider;
		this.opmode = cipherFactory.opmode;
		this.key = cipherFactory.key;
		this.params = cipherFactory.params;
		this.secureRandom = cipherFactory.secureRandom;
	}

	@Override
	public CipherFactory clone() {
		return new CipherFactory(this);
	}

	public int getOpmode() {
		return opmode;
	}

	public String getTransformation() {
		return transformation;
	}

	public Object getKey() {
		return key;
	}

	public Object getParams() {
		return params;
	}

	public SecureRandom getSecureRandom() {
		return secureRandom;
	}

	public Object getProvider() {
		return provider;
	}

	public Cipher getCipher() throws CodecException, NoSuchAlgorithmException, NoSuchPaddingException,
			NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
		Cipher cipher = threadLocal.get();
		if (cipher != null) {
			return cipher;
		}

		if (provider == null) {
			cipher = Cipher.getInstance(transformation);
		} else if (provider instanceof String) {
			cipher = Cipher.getInstance(transformation, (String) provider);
		} else if (provider instanceof Provider) {
			cipher = Cipher.getInstance(transformation, (Provider) provider);
		} else {
			throw new CodecException(provider.toString());
		}

		try {
			ReflectionUtils.invokeOverloadMethod(cipher, "init", true, opmode, key, params, secureRandom);
		} catch (NoSuchMethodException e) {
			throw new CodecException(e);
		}
		threadLocal.set(cipher);
		return cipher;
	}

	public <E extends Throwable> long doFinal(InputStream source, int bufferSize,
			BufferProcessor<byte[], E> targetProcessor) throws IOException, CodecException, E {
		Cipher cipher;
		try {
			cipher = getCipher();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException
				| InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}

		return IOUtils.read(source, bufferSize, (buff, offset, len) -> {
			byte[] target;
			try {
				target = cipher.doFinal(buff, offset, len);
			} catch (IllegalBlockSizeException | BadPaddingException e) {
				throw new CodecException(e);
			}
			targetProcessor.process(target, 0, target.length);
		});
	}

	public byte[] doFinal(byte[] source) throws CodecException {
		Cipher cipher;
		try {
			cipher = getCipher();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException
				| InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}
		try {
			return cipher.doFinal(source);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CodecException(e);
		}
	}

	public byte[] doFinal(byte[] source, int offset, int len) throws CodecException {
		Cipher cipher;
		try {
			cipher = getCipher();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException
				| InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}
		try {
			return cipher.doFinal(source, offset, len);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CodecException(e);
		}
	}
}
