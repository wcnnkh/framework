package io.basc.framework.util.codec.encode;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;

public class SHA1WithRSASigner extends AsymmetricSigner {
	public static final String SHA1_WITH_RSA = "SHA1WithRSA";

	public SHA1WithRSASigner(PrivateKey privateKey, PublicKey publicKey) {
		this(privateKey, null, publicKey);
	}

	public SHA1WithRSASigner(PrivateKey privateKey, SecureRandom secureRandom, PublicKey publicKey) {
		super(SHA1_WITH_RSA, privateKey, secureRandom, publicKey);
	}

	public SHA1WithRSASigner(PrivateKey privateKey, SecureRandom secureRandom, Certificate certificate) {
		super(SHA1_WITH_RSA, privateKey, secureRandom, certificate);
	}

	protected SHA1WithRSASigner(SHA1WithRSASigner signer) {
		super(signer);
	}

	@Override
	public SHA1WithRSASigner clone() {
		return new SHA1WithRSASigner(this);
	}
}
