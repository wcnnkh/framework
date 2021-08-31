package io.basc.framework.codec.sign;

import java.security.PrivateKey;
import java.security.PublicKey;

public class SHA1WithRSASigner extends AsymmetricSigner{

	public SHA1WithRSASigner(PrivateKey privateKey,
			PublicKey publicKey) {
		super(AsymmetricSigner.SHA1_WITH_RSA, privateKey, publicKey);
	}
	
}
