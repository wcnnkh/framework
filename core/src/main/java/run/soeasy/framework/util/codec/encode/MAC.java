package run.soeasy.framework.util.codec.encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.NonNull;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.codec.CodecException;
import run.soeasy.framework.util.codec.EncodeException;
import run.soeasy.framework.util.io.IOUtils;

/**
 * 
 * HmacMD5 HmacSHA1 HmacSHA256
 * 
 * @author wcnnkh
 *
 */
public class MAC implements BytesEncoder, Cloneable {
	private final Key key;
	private final AlgorithmParameterSpec algorithmParameterSpec;

	public MAC(String algorithm, byte[] key) {
		this(algorithm, key, null);
	}

	public MAC(@NonNull String algorithm, @NonNull byte[] key, AlgorithmParameterSpec algorithmParameterSpec) {
		this(new SecretKeySpec(key, algorithm), algorithmParameterSpec);
	}

	public MAC(Key key) {
		this(key, (AlgorithmParameterSpec) null);
	}

	public MAC(@NonNull Key key, AlgorithmParameterSpec algorithmParameterSpec) {
		Assert.requiredArgument(key != null, "key");
		this.key = key;
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	protected MAC(MAC mac) {
		this.key = mac.key;
		this.algorithmParameterSpec = mac.algorithmParameterSpec;
	}

	public Key getKey() {
		return key;
	}

	public AlgorithmParameterSpec getAlgorithmParameterSpec() {
		return algorithmParameterSpec;
	}

	@Override
	public MAC clone() {
		return new MAC(this);
	}

	public Mac getMac() throws CodecException {
		String algorithm = this.key.getAlgorithm();
		Mac mac;
		try {
			mac = Mac.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CodecException(algorithm, e);
		}

		try {
			if (algorithmParameterSpec == null) {
				mac.init(key);
			} else {
				mac.init(key, algorithmParameterSpec);

			}
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new CodecException(key.getAlgorithm(), e);
		}
		return mac;
	}

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		Mac mac = getMac();
		IOUtils.read(source, bufferSize, mac::update);
		byte[] response = mac.doFinal();
		target.write(response);
	}
}
