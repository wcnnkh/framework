package io.basc.framework.codec.support;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import io.basc.framework.codec.CodecException;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public class CipherFactory implements Cloneable {
	private final ThreadLocal<Cipher[]> threadLocal;
	private final String transformation;
	private final Object key;
	private final Object params;
	private final SecureRandom secureRandom;
	private final Object provider;

	public CipherFactory(String transformation, @Nullable String provider, Certificate key,
			@Nullable AlgorithmParameterSpec params, @Nullable SecureRandom secureRandom) {
		this(transformation, (Object) provider, (Object) key, (Object) params, secureRandom);
	}

	public CipherFactory(String transformation, @Nullable String provider, Certificate key,
			@Nullable AlgorithmParameters params, @Nullable SecureRandom secureRandom) {
		this(transformation, (Object) provider, (Object) key, (Object) params, secureRandom);
	}

	public CipherFactory(String transformation, @Nullable String provider, Key key,
			@Nullable AlgorithmParameterSpec params, @Nullable SecureRandom secureRandom) {
		this(transformation, (Object) provider, (Object) key, (Object) params, secureRandom);
	}

	public CipherFactory(String transformation, @Nullable String provider, Key key,
			@Nullable AlgorithmParameters params, @Nullable SecureRandom secureRandom) {
		this(transformation, (Object) provider, (Object) key, (Object) params, secureRandom);
	}

	public CipherFactory(String transformation, @Nullable Provider provider, Certificate key,
			@Nullable AlgorithmParameterSpec params, @Nullable SecureRandom secureRandom) {
		this(transformation, (Object) provider, (Object) key, (Object) params, secureRandom);
	}

	public CipherFactory(String transformation, @Nullable Provider provider, Certificate key,
			@Nullable AlgorithmParameters params, @Nullable SecureRandom secureRandom) {
		this(transformation, (Object) provider, (Object) key, (Object) params, secureRandom);
	}

	public CipherFactory(String transformation, @Nullable Provider provider, Key key,
			@Nullable AlgorithmParameterSpec params, @Nullable SecureRandom secureRandom) {
		this(transformation, (Object) provider, (Object) key, (Object) params, secureRandom);
	}

	public CipherFactory(String transformation, @Nullable Provider provider, Key key,
			@Nullable AlgorithmParameters params, @Nullable SecureRandom secureRandom) {
		this(transformation, (Object) provider, (Object) key, (Object) params, secureRandom);
	}

	public CipherFactory(String transformation, @Nullable Object provider, Object key, @Nullable Object params,
			@Nullable SecureRandom secureRandom) {
		Assert.requiredArgument(StringUtils.hasText(transformation), "transformation");
		Assert.requiredArgument(key != null, "key");
		this.threadLocal = new NamedThreadLocal<Cipher[]>(transformation);
		this.transformation = transformation;
		this.provider = provider;
		this.key = key;
		this.params = params;
		this.secureRandom = secureRandom;
	}

	protected CipherFactory(CipherFactory cipherFactory) {
		this.threadLocal = cipherFactory.threadLocal;
		this.transformation = cipherFactory.transformation;
		this.provider = cipherFactory.provider;
		this.key = cipherFactory.key;
		this.params = cipherFactory.params;
		this.secureRandom = cipherFactory.secureRandom;
	}

	@Override
	public CipherFactory clone() {
		return new CipherFactory(this);
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

	public Cipher getCipher(int opmode) throws CodecException, NoSuchAlgorithmException, NoSuchPaddingException,
			NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
		Cipher[] ciphers = threadLocal.get();
		if (ciphers == null) {
			// 容量为4, 因为只有4种模式
			ciphers = new Cipher[4];
		}

		if (opmode > ciphers.length) {
			throw new CodecException(transformation + "[" + opmode + "]");
		}

		Cipher cipher = ciphers[opmode];
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

		if (params == null) {
			if (key instanceof Certificate) {
				if (secureRandom == null) {
					cipher.init(opmode, (Certificate) key);
				} else {
					cipher.init(opmode, (Certificate) key, secureRandom);
				}
			} else if (key instanceof Key) {
				if (secureRandom == null) {
					cipher.init(opmode, (Key) key);
				} else {
					cipher.init(opmode, (Key) key, secureRandom);
				}
			}
		} else {
			if (params instanceof AlgorithmParameterSpec) {
				if (secureRandom == null) {
					cipher.init(opmode, (Key) key, (AlgorithmParameterSpec) params);
				} else {
					cipher.init(opmode, (Key) key, (AlgorithmParameterSpec) secureRandom);
				}
			} else if (params instanceof AlgorithmParameters) {
				if (secureRandom == null) {
					cipher.init(opmode, (Key) key, (AlgorithmParameters) params);
				} else {
					cipher.init(opmode, (Key) key, (AlgorithmParameters) params, secureRandom);
				}
			}
		}
		ciphers[opmode] = cipher;
		threadLocal.set(ciphers);
		return cipher;
	}
}
