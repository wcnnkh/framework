package run.soeasy.framework.codec.security;

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

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.BytesEncoder;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.io.IOUtils;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

/**
 * 非对称加密签名
 * 
 * @author wcnnkh
 *
 */
public class AsymmetricSigner implements BytesEncoder, Cloneable {
	private static Logger logger = LogManager.getLogger(AsymmetricSigner.class);

	private final String algorithm;
	private final Object verifyKey;
	private final PrivateKey privateKey;
	private final SecureRandom secureRandom;

	public AsymmetricSigner(@NonNull String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this(algorithm, privateKey, null, publicKey);
	}

	public AsymmetricSigner(@NonNull String algorithm, PrivateKey privateKey, SecureRandom secureRandom,
			PublicKey publicKey) {
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.verifyKey = publicKey;
		this.secureRandom = secureRandom;
	}

	public AsymmetricSigner(@NonNull String algorithm, PrivateKey privateKey, Certificate certificate) {
		this(algorithm, privateKey, null, certificate);
	}

	public AsymmetricSigner(@NonNull String algorithm, PrivateKey privateKey, SecureRandom secureRandom,
			Certificate certificate) {
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.verifyKey = certificate;
		this.secureRandom = secureRandom;
	}

	protected AsymmetricSigner(AsymmetricSigner signer) {
		this.algorithm = signer.algorithm;
		this.privateKey = signer.privateKey;
		this.verifyKey = signer.verifyKey;
		this.secureRandom = signer.secureRandom;
	}

	@Override
	public AsymmetricSigner clone() {
		return new AsymmetricSigner(this);
	}

	public Signature getEncodeSignature() throws CodecException {
		try {
			Signature signature = Signature.getInstance(algorithm);
			if (secureRandom == null) {
				signature.initSign(privateKey);
			} else {
				signature.initSign(privateKey, secureRandom);
			}
			return signature;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new CodecException(algorithm, e);
		}
	}

	public Signature getVerifySignature() throws CodecException {
		try {
			Signature signature = Signature.getInstance(algorithm);
			if (verifyKey instanceof PublicKey) {
				signature.initVerify((PublicKey) verifyKey);
			} else if (verifyKey instanceof Certificate) {
				signature.initVerify((Certificate) verifyKey);
			} else {
				// 不支持的类型
				throw new CodecException(verifyKey.toString());
			}
			return signature;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new CodecException(algorithm, e);
		}
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
