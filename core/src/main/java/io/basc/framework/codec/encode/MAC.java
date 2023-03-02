package io.basc.framework.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.basc.framework.codec.CodecException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.IOUtils;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

/**
 * 
 * HmacMD5 HmacSHA1 HmacSHA256
 * 
 * @author wcnnkh
 *
 */
public class MAC implements BytesEncoder, Cloneable {
	private final NamedThreadLocal<Mac> threadLocal;
	private final Key key;
	private final AlgorithmParameterSpec algorithmParameterSpec;

	public MAC(String algorithm, byte[] key) {
		this(algorithm, key, null);
	}

	public MAC(String algorithm, byte[] key, @Nullable AlgorithmParameterSpec algorithmParameterSpec) {
		this(new SecretKeySpec(key, algorithm), algorithmParameterSpec);
	}

	public MAC(Key key) {
		this(key, (AlgorithmParameterSpec) null);
	}

	public MAC(Key key, @Nullable AlgorithmParameterSpec algorithmParameterSpec) {
		Assert.requiredArgument(key != null, "key");
		this.key = key;
		this.algorithmParameterSpec = algorithmParameterSpec;
		this.threadLocal = new NamedThreadLocal<Mac>(key.getAlgorithm());
	}

	protected MAC(MAC mac) {
		this.key = mac.key;
		this.algorithmParameterSpec = mac.algorithmParameterSpec;
		this.threadLocal = mac.threadLocal;
	}

	public Key getKey() {
		return key;
	}

	public AlgorithmParameterSpec getAlgorithmParameterSpec() {
		return algorithmParameterSpec;
	}

	@Override
	public MAC clone() {
		return new MAC(this);
	}

	public Mac getMac() throws CodecException {
		Mac mac = threadLocal.get();
		if (mac != null) {
			mac.reset();
			return mac;
		}

		mac = getMac(this.key.getAlgorithm());
		try {
			if (algorithmParameterSpec == null) {
				mac.init(key);
			} else {
				mac.init(key, algorithmParameterSpec);

			}
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new CodecException(key.getAlgorithm(), e);
		}
		threadLocal.set(mac);
		return mac;
	}

	public static Mac getMac(String algorithm) throws CodecException {
		try {
			return Mac.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CodecException(algorithm, e);
		}
	}

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		Mac mac = getMac();
		IOUtils.read(source, bufferSize, mac::update);
		byte[] response = mac.doFinal();
		target.write(response);
	}
}
