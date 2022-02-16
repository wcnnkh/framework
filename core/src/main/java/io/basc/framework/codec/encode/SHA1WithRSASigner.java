package io.basc.framework.codec.encode;

import java.security.PrivateKey;
import java.security.PublicKey;

public class SHA1WithRSASigner extends AsymmetricSigner {
	public static final String SHA1_WITH_RSA = "SHA1WithRSA";

	public SHA1WithRSASigner(PrivateKey privateKey, PublicKey publicKey) {
		super(SHA1_WITH_RSA, privateKey, publicKey);
	}

}
