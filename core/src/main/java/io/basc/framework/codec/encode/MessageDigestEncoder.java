package io.basc.framework.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.basc.framework.codec.CodecException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.IOUtils;

public class MessageDigestEncoder implements BytesEncoder {
	protected final String algorithm;
	private byte[] secretKey;

	public MessageDigestEncoder(String algorithm) {
		this.algorithm = algorithm;
	}

	public MessageDigest getMessageDigest() {
		return getMessageDigest(algorithm);
	}

	public MessageDigestEncoder wrapperSecretKey(byte[] secretKey) {
		MessageDigestEncoder signer = new MessageDigestEncoder(algorithm);
		signer.secretKey = secretKey;
		return signer;
	}

	public static MessageDigest getMessageDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CodecException(algorithm, e);
		}
	}

	@Override
	public String toString() {
		return algorithm;
	}

	@Override
	public byte[] encode(InputStream source, int bufferSize) throws IOException, EncodeException {
		MessageDigest messageDigest = getMessageDigest();
		messageDigest.reset();
		if (secretKey != null) {
			messageDigest.update(secretKey);
		}
		IOUtils.read(source, bufferSize, messageDigest::update);
		return messageDigest.digest();
	}

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		byte[] res = encode(source, bufferSize);
		target.write(res);
	}
}
