package io.basc.framework.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		Mac mac = getMac();
		mac.reset();
		IOUtils.read(source, bufferSize, mac::update);
		byte[] response = mac.doFinal();
		target.write(response);
	}
}
