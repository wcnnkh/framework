package io.basc.framework.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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

	public byte[] encode(byte[] source) throws EncodeException {
		MessageDigest messageDigest = getMessageDigest();
		messageDigest.reset();

		if (secretKey == null) {
			messageDigest.update(source);
		} else {
			byte[] secretSource = Arrays.copyOf(source, source.length + secretKey.length);
			System.arraycopy(secretKey, 0, secretSource, source.length, secretKey.length);
			messageDigest.update(secretSource);
		}
		return messageDigest.digest();
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
	public byte[] encode(byte[] encode, int count) throws EncodeException {
		return encode(getMessageDigest(), encode, count);
	}

	public byte[] encode(MessageDigest messageDigest, byte[] encode, int count) throws EncodeException {
		byte[] res = encode;
		for (int i = 0; i < count; i++) {
			messageDigest.reset();
			if (secretKey != null) {
				messageDigest.update(secretKey);
			}

			messageDigest.update(res);
			res = messageDigest.digest();
		}
		return res;
	}

	@Override
	public byte[] encode(InputStream source, int bufferSize, int count) throws IOException, EncodeException {
		MessageDigest messageDigest = getMessageDigest();
		messageDigest.reset();
		if (secretKey != null) {
			messageDigest.update(secretKey);
		}
		IOUtils.read(source, bufferSize, messageDigest::update);
		byte[] res = messageDigest.digest();
		if (count > 1) {
			return encode(messageDigest, res, count - 1);
		}
		return res;
	}
}
