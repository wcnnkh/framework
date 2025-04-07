package run.soeasy.framework.util.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import run.soeasy.framework.util.codec.CodecException;
import run.soeasy.framework.util.codec.EncodeException;
import run.soeasy.framework.util.io.IOUtils;

public class MessageDigestEncoder implements BytesEncoder, Cloneable {
	protected final String algorithm;
	private byte[] secretKey;

	public MessageDigestEncoder(String algorithm) {
		this.algorithm = algorithm;
	}

	protected MessageDigestEncoder(MessageDigestEncoder encoder) {
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
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CodecException(algorithm, e);
		}
	}

	public MessageDigestEncoder wrapperSecretKey(byte[] secretKey) {
		MessageDigestEncoder signer = clone();
		signer.secretKey = secretKey;
		return signer;
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
