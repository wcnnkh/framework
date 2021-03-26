package scw.codec.support;

import java.security.spec.AlgorithmParameterSpec;


public class AES extends SymmetricCodec{
	public static final String ALGORITHM = "AES";
	
	/**
	 * @see SymmetricCodec#NO_PADDING
	 * @param secreKey
	 * @param ivKey
	 */
	public AES(byte[] secreKey, byte[] ivKey){
		this(FILL_STYLE, secreKey, ivKey);
	}
	
	/**
	 * @param fillStyle 填充方式
	 * @param secreKey
	 * @param ivKey
	 */
	public AES(String fillStyle, byte[] secreKey, byte[] ivKey) {
		super(ALGORITHM, fillStyle, secreKey, ivKey);
	}
	
	/**
	 * @param fillStyle 填充方式
	 * @param secreKey
	 * @param algorithmParameterSpec
	 */
	public AES(String fillStyle, byte[] secreKey, AlgorithmParameterSpec algorithmParameterSpec) {
		super(ALGORITHM, fillStyle, secreKey, algorithmParameterSpec);
	}
	
	/**
	 * @param workMode 工作模式
	 * @param fillStyle 填充方式
	 * @param secretKey
	 * @param algorithmParameterSpec
	 */
	public AES(String workMode, String fillStyle, byte[] secretKey,
			AlgorithmParameterSpec algorithmParameterSpec){
		super(ALGORITHM, workMode, fillStyle, secretKey, algorithmParameterSpec);
	}
}
