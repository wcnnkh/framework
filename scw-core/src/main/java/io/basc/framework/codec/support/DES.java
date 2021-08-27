package io.basc.framework.codec.support;

import java.security.spec.AlgorithmParameterSpec;


/**
 * DES
 * 
 * @author shuchaowen
 *
 */
public class DES extends SymmetricCodec {
	public static final String ALGORITHM = "DES";
	public static final String FILL_STYLE = "PKCS5Padding";
	
	public DES(byte[] secreKey, byte[] ivKey){
		this(FILL_STYLE, secreKey, ivKey);
	}
	
	public DES(String fillStyle, byte[] secreKey, byte[] ivKey) {
		super(ALGORITHM, fillStyle, secreKey, ivKey);
	}
	
	public DES(String fillStyle, byte[] secreKey, AlgorithmParameterSpec algorithmParameterSpec) {
		super(ALGORITHM, fillStyle, secreKey, algorithmParameterSpec);
	}
	
	public DES(String workMode, String fillStyle, byte[] secretKey,
			AlgorithmParameterSpec algorithmParameterSpec){
		super(ALGORITHM, workMode, fillStyle, secretKey, algorithmParameterSpec);
	}
}
