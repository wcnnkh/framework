package io.basc.framework.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.basc.framework.codec.CodecException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.IOUtils;
import io.basc.framework.lang.NamedThreadLocal;

public class MessageDigestEncoder implements BytesEncoder, Cloneable {
	private final NamedThreadLocal<MessageDigest> threadLocal;

	protected final String algorithm;
	private byte[] secretKey;

	public MessageDigestEncoder(String algorithm) {
		this.algorithm = algorithm;
		threadLocal = new NamedThreadLocal<MessageDigest>(algorithm);
	}

	protected MessageDigestEncoder(MessageDigestEncoder encoder) {
		this.threadLocal = encoder.threadLocal;
		this.algorithm = encoder.algorithm;
		this.secretKey = encoder.secretKey;
	}

	public byte[] getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(byte[] secretKey) {
		this.secretKey = secretKey;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	public MessageDigestEncoder clone() {
		return new MessageDigestEncoder(this);
	}

	public MessageDigest getMessageDigest() {
		MessageDigest messageDigest = threadLocal.get();
		if (messageDigest != null) {
			messageDigest.reset();
			return messageDigest;
		}

		messageDigest = getMessageDigest(algorithm);
		threadLocal.set(messageDigest);
		return messageDigest;
	}

	public MessageDigestEncoder wrapperSecretKey(byte[] secretKey) {
		MessageDigestEncoder signer = clone();
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
