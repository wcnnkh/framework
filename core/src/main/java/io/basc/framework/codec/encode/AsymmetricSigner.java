package io.basc.framework.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.IOUtils;
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
public class AsymmetricSigner implements BytesEncoder {
	private static Logger logger = LoggerFactory.getLogger(AsymmetricSigner.class);

	private final String algorithm;
	private final PrivateKey privateKey;
	private final PublicKey publicKey;

	public AsymmetricSigner(String algorithm, @Nullable PrivateKey privateKey, @Nullable PublicKey publicKey) {
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	@Override
	public boolean verify(InputStream source, int bufferSize, byte[] target) throws EncodeException, IOException {
		Assert.requiredArgument(publicKey != null, "publicKey");
		try {
			Signature signature = java.security.Signature.getInstance(algorithm);
			signature.initVerify(publicKey);
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
			Signature signature = java.security.Signature.getInstance(algorithm);
			signature.initSign(privateKey);
			IOUtils.read(source, bufferSize, signature::update);
			byte[] sign = signature.sign();
			target.write(sign);
		} catch (Exception e) {
			throw new EncodeException(e);
		}
	}
}
