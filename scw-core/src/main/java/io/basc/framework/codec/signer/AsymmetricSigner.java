package io.basc.framework.codec.signer;

import io.basc.framework.codec.EncodeException;
import io.basc.framework.core.Assert;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * 非对称加密签名
 * 
 * @author shuchaowen
 *
 */
public class AsymmetricSigner implements BytesSigner<byte[]> {
	private static Logger logger = LoggerFactory
			.getLogger(AsymmetricSigner.class);
	
	public static final String SHA1_WITH_RSA = "SHA1WithRSA";
	
	private final String algorithm;
	private final PrivateKey privateKey;
	private final PublicKey publicKey;

	public AsymmetricSigner(String algorithm, @Nullable PrivateKey privateKey,
			@Nullable PublicKey publicKey) {
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public byte[] encode(byte[] source) throws EncodeException {
		Assert.requiredArgument(privateKey != null, "privateKey");
		try {
			Signature signature = java.security.Signature
					.getInstance(algorithm);
			signature.initSign(privateKey);
			signature.update(source);
			return signature.sign();
		} catch (Exception e) {
			throw new EncodeException(e);
		}
	}

	public boolean verify(byte[] source, byte[] sign) {
		Assert.requiredArgument(publicKey != null, "publicKey");
		try {
			Signature signature = java.security.Signature
					.getInstance(algorithm);
			signature.initVerify(publicKey);
			signature.update(source);
			return signature.verify(sign);
		} catch (Exception e) {
			logger.error(e, "verify error");
		}
		return false;
	}

}
