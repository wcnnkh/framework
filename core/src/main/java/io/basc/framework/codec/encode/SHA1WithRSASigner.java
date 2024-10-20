package io.basc.framework.codec.encode;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;

import io.basc.framework.lang.Nullable;

public class SHA1WithRSASigner extends AsymmetricSigner {
	public static final String SHA1_WITH_RSA = "SHA1WithRSA";

	public SHA1WithRSASigner(@Nullable PrivateKey privateKey, @Nullable PublicKey publicKey) {
		this(privateKey, null, publicKey);
	}

	public SHA1WithRSASigner(@Nullable PrivateKey privateKey, @Nullable SecureRandom secureRandom,
			@Nullable PublicKey publicKey) {
		super(SHA1_WITH_RSA, privateKey, secureRandom, publicKey);
	}

	public SHA1WithRSASigner(@Nullable PrivateKey privateKey, @Nullable SecureRandom secureRandom,
			@Nullable Certificate certificate) {
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
