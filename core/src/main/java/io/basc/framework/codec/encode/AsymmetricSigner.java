package io.basc.framework.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;

import io.basc.framework.codec.CodecException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.IOUtils;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;

/**
 * 非对称加密签名
 * 
 * @author shuchaowen
 *
 */
public class AsymmetricSigner implements BytesEncoder, Cloneable {
	private static Logger logger = LoggerFactory.getLogger(AsymmetricSigner.class);

	private final String algorithm;
	private final Object verifyKey;
	private final PrivateKey privateKey;
	private final NamedThreadLocal<Signature> encodeLocal;
	private final NamedThreadLocal<Signature> verifyLocal;
	private final SecureRandom secureRandom;

	public AsymmetricSigner(String algorithm, @Nullable PrivateKey privateKey, @Nullable PublicKey publicKey) {
		this(algorithm, privateKey, null, publicKey);
	}

	public AsymmetricSigner(String algorithm, @Nullable PrivateKey privateKey, @Nullable SecureRandom secureRandom,
			@Nullable PublicKey publicKey) {
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.verifyKey = publicKey;
		this.secureRandom = secureRandom;
		this.encodeLocal = new NamedThreadLocal<>(algorithm);
		this.verifyLocal = new NamedThreadLocal<Signature>(algorithm);
	}

	public AsymmetricSigner(String algorithm, @Nullable PrivateKey privateKey, @Nullable Certificate certificate) {
		this(algorithm, privateKey, null, certificate);
	}

	public AsymmetricSigner(String algorithm, @Nullable PrivateKey privateKey, @Nullable SecureRandom secureRandom,
			@Nullable Certificate certificate) {
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.verifyKey = certificate;
		this.secureRandom = secureRandom;
		this.encodeLocal = new NamedThreadLocal<>(algorithm);
		this.verifyLocal = new NamedThreadLocal<Signature>(algorithm);
	}

	protected AsymmetricSigner(AsymmetricSigner signer) {
		this.algorithm = signer.algorithm;
		this.privateKey = signer.privateKey;
		this.verifyKey = signer.verifyKey;
		this.secureRandom = signer.secureRandom;
		this.encodeLocal = signer.encodeLocal;
		this.verifyLocal = signer.verifyLocal;
	}

	@Override
	public AsymmetricSigner clone() {
		return new AsymmetricSigner(this);
	}

	public Signature getEncodeSignature() throws CodecException {
		Signature signature = encodeLocal.get();
		if (signature != null) {
			return signature;
		}

		try {
			signature = Signature.getInstance(algorithm);
			if (secureRandom == null) {
				signature.initSign(privateKey);
			} else {
				signature.initSign(privateKey, secureRandom);
			}
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new CodecException(algorithm, e);
		}
		encodeLocal.set(signature);
		return signature;
	}

	public Signature getVerifySignature() throws CodecException {
		Signature signature = verifyLocal.get();
		if (signature != null) {
			return signature;
		}

		try {
			signature = Signature.getInstance(algorithm);
			if (verifyKey instanceof PublicKey) {
				signature.initVerify((PublicKey) verifyKey);
			} else if (verifyKey instanceof Certificate) {
				signature.initVerify((Certificate) verifyKey);
			} else {
				// 不支持的类型
				throw new CodecException(verifyKey.toString());
			}
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new CodecException(algorithm, e);
		}
		verifyLocal.set(signature);
		return signature;
	}

	@Override
	public boolean verify(InputStream source, int bufferSize, byte[] target) throws EncodeException, IOException {
		if (verifyKey == null) {
			return BytesEncoder.super.verify(source, bufferSize, target);
		}

		try {
			Signature signature = getVerifySignature();
			IOUtils.read(source, bufferSize, signature::update);
			return signature.verify(target);
		} catch (Exception e) {
			logger.error(e, "verify error");
		}
		return false;
	}

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		Assert.requiredArgument(privateKey != null, "privateKey");
		try {
			Signature signature = getEncodeSignature();
			IOUtils.read(source, bufferSize, signature::update);
			byte[] sign = signature.sign();
			target.write(sign);
		} catch (Exception e) {
			throw new EncodeException(e);
		}
	}
}
