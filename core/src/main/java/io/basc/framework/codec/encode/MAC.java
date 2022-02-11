package io.basc.framework.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.basc.framework.codec.CodecException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.IOUtils;

/**
 * 
 * HmacMD5<br/>
 * HmacSHA1<br/>
 * HmacSHA256<br/>
 * 
 * @author shuchaowen
 *
 */
public class MAC implements BytesEncoder {
	private final SecretKey secretKey;

	public MAC(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	public MAC(String algorithm, byte[] secretKey) {
		this(getSecretKey(algorithm, secretKey));
	}

	public Mac getMac() throws CodecException {
		Mac mac = getMac(secretKey.getAlgorithm());
		try {
			mac.init(secretKey);
		} catch (InvalidKeyException e) {
			throw new CodecException(e);
		}
		return mac;
	}

	public static Mac getMac(String algorithm) throws CodecException {
		try {
			return Mac.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CodecException(algorithm, e);
		}
	}

	public static SecretKey getSecretKey(String algorithm, byte[] secretKey) {
		return new SecretKeySpec(secretKey, algorithm);
	}

	public byte[] encode(byte[] source, int count) throws EncodeException {
		return encode(getMac(), source, count);
	}

	public byte[] encode(Mac mac, byte[] source, int count) throws EncodeException {
		byte[] res = source;
		for (int i = 0; i < count; i++) {
			mac.reset();
			mac.update(res);
			res = mac.doFinal();
		}
		return res;
	}

	@Override
	public byte[] encode(InputStream source, int bufferSize, int count) throws IOException, EncodeException {
		Mac mac = getMac();
		mac.reset();
		IOUtils.read(source, bufferSize, mac::update);
		byte[] response = mac.doFinal();
		if (count > 1) {
			return encode(mac, response, count - 1);
		}
		return response;
	}

}
