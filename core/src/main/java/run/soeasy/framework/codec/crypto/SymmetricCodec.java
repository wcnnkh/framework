package run.soeasy.framework.codec.crypto;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.spec.IvParameterSpec;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;

/**
 * 对称编解码器
 * 
 * @author wcnnkh
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
		this(algorithm, workMode, fillStyle, secretKey, ivKey == null ? null : new IvParameterSpec(ivKey));
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
		this(algorithm + "/" + (StringUtils.isEmpty(workMode) ? WORK_MODE : workMode) + "/"
				+ (StringUtils.isEmpty(fillStyle) ? FILL_STYLE : fillStyle), null, (cipher, opmode) -> {
					cipher.init(opmode, getSecretKey(algorithm, secretKey), algorithmParameterSpec);
				});
	}

	public SymmetricCodec(@NonNull String transformation, Object provider,
			@NonNull CipherInitializer cipherInitializer) {
		super(transformation, provider, cipherInitializer, cipherInitializer);
	}
}
