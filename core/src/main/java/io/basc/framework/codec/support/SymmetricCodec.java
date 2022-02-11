package io.basc.framework.codec.support;

import io.basc.framework.codec.CodecException;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.IOUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * 对称编解码器
 * 
 * @author shuchaowen
 *
 */
public class SymmetricCodec extends CryptoCodec {
	/**
	 * 默认的工作模式
	 */
	public static final String WORK_MODE = "CBC";

	/**
	 * 默认的填充方式
	 */
	public static final String FILL_STYLE = "PKCS5Padding";

	public static final String NO_PADDING = "NoPadding";

	private final String algorithm;
	private final String workMode;// 工作模式
	private final String fillStyle;// 填充方式

	private final SecretKey secretKey;
	private final AlgorithmParameterSpec algorithmParameterSpec;

	/**
	 * 使用默认的工作模式CBC
	 * 
	 * @param algorithm              算法名称，如AES
	 * @param fillStyle              填充方式，如PKCS7Padding
	 * @param secretKey
	 * @param algorithmParameterSpec
	 * @see #WORK_MODE
	 * @see #SymmetricCodec(String, String, String, byte[], AlgorithmParameterSpec)
	 */
	public SymmetricCodec(String algorithm, String fillStyle, byte[] secretKey,
			AlgorithmParameterSpec algorithmParameterSpec) {
		this(algorithm, WORK_MODE, fillStyle, secretKey, algorithmParameterSpec);
	}

	/**
	 * 使用默认的工作模式CBC
	 * 
	 * @param algorithm 算法名称，如AES
	 * @param fillStyle 填充方式，如PKCS7Padding
	 * @param secretKey
	 * @param ivKey
	 * @see #WORK_MODE
	 * @see #SymmetricCodec(String, String, String, byte[], byte[])
	 */
	public SymmetricCodec(String algorithm, String fillStyle, byte[] secretKey, byte[] ivKey) {
		this(algorithm, WORK_MODE, fillStyle, secretKey, ivKey);
	}

	/**
	 * @param algorithm 算法名称，如AES
	 * @param workMode  工作模式，如CBC
	 * @param fillStyle 填充方式，如PKCS7Padding
	 * @param secretKey
	 * @param ivKey
	 */
	public SymmetricCodec(String algorithm, String workMode, String fillStyle, byte[] secretKey, byte[] ivKey) {
		this.algorithm = algorithm;
		this.workMode = workMode;
		this.fillStyle = fillStyle;
		this.secretKey = getSecretKey(algorithm, secretKey);
		this.algorithmParameterSpec = createAlgorithmParameterSpec(ivKey);
	}

	/**
	 * @param algorithm              算法名称，如AES
	 * @param workMode               工作模式，如CBC
	 * @param fillStyle              填充方式，如PKCS7Padding
	 * @param secretKey
	 * @param algorithmParameterSpec
	 */
	public SymmetricCodec(String algorithm, String workMode, String fillStyle, byte[] secretKey,
			AlgorithmParameterSpec algorithmParameterSpec) {
		Assert.requiredArgument(StringUtils.hasText(algorithm), "algorithm");
		this.algorithm = algorithm;
		this.workMode = workMode;
		this.fillStyle = fillStyle;
		this.secretKey = getSecretKey(algorithm, secretKey);
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	protected AlgorithmParameterSpec createAlgorithmParameterSpec(byte[] ivKey) {
		return new IvParameterSpec(ivKey);
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String getWorkMode() {
		return workMode == null ? WORK_MODE : workMode;
	}

	public String getFillStyle() {
		return fillStyle == null ? FILL_STYLE : fillStyle;
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public AlgorithmParameterSpec getAlgorithmParameterSpec() {
		return algorithmParameterSpec;
	}

	public String getCompleteAlgorithm() {
		return getAlgorithm() + "/" + getWorkMode() + "/" + getFillStyle();
	}

	public Cipher getCipher() {
		return getCipher(getCompleteAlgorithm());
	}

	@Override
	public String toString() {
		return getCompleteAlgorithm();
	}

	public byte[] doFinal(Cipher cipher, byte[] source, int count)
			throws IllegalBlockSizeException, BadPaddingException {
		byte[] res = source;
		for (int i = 0; i < count; i++) {
			cipher.update(res);
			// 执行并重置
			res = cipher.doFinal();
		}
		return res;
	}

	public byte[] doFinal(Cipher cipher, InputStream source, int bufferSize, int count)
			throws IOException, IllegalBlockSizeException, BadPaddingException {
		IOUtils.read(source, bufferSize, cipher::update);
		byte[] res = cipher.doFinal();
		if (count > 1) {
			return doFinal(cipher, res, count - 1);
		}
		return res;
	}

	@Override
	public byte[] encode(byte[] source, int count) throws EncodeException {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, algorithmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}

		try {
			return doFinal(cipher, source, count);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new EncodeException(e);
		}
	}

	@Override
	public byte[] decode(byte[] source, int count) throws DecodeException {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey, algorithmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}

		try {
			return doFinal(cipher, source, count);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new DecodeException(e);
		}
	}

	@Override
	public byte[] encode(InputStream source, int bufferSize, int count) throws IOException, EncodeException {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, algorithmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}

		try {
			return doFinal(cipher, source, bufferSize, count);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new DecodeException(e);
		}
	}

	@Override
	public byte[] decode(InputStream source, int bufferSize, int count) throws IOException, DecodeException {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey, algorithmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new CodecException(e);
		}

		try {
			return doFinal(cipher, source, bufferSize, count);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new DecodeException(e);
		}
	}
}
