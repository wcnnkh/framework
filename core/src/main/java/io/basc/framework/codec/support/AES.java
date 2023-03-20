package io.basc.framework.codec.support;

import java.security.spec.AlgorithmParameterSpec;

public class AES extends SymmetricCodec {
	public static final String ALGORITHM = "AES";

	public AES(byte[] secreKey, byte[] ivKey) {
		this(FILL_STYLE, secreKey, ivKey);
	}

	public AES(String fillStyle, byte[] secreKey, byte[] ivKey) {
		super(ALGORITHM, fillStyle, secreKey, ivKey);
	}

	public AES(String fillStyle, byte[] secreKey, AlgorithmParameterSpec algorithmParameterSpec) {
		super(ALGORITHM, fillStyle, secreKey, algorithmParameterSpec);
	}

	public AES(String workMode, String fillStyle, byte[] secretKey, AlgorithmParameterSpec algorithmParameterSpec) {
		super(ALGORITHM, workMode, fillStyle, secretKey, algorithmParameterSpec);
	}
}
