package scw.codec.support;

import java.security.Key;

import scw.codec.Signer;
import scw.core.Assert;
import scw.lang.Nullable;


/**
 * RSA
 * @author shuchaowen
 *
 */
public class RSA extends AsymmetricCodec {
	/**
	 * ALGORITHM ['ælgərɪð(ə)m] 算法的意思
	 */
	public static final String ALGORITHM = "RSA";
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	
	private final Signer<byte[], byte[]> signer;
	
	/**
	 * 公钥和私钥应该至少存在一个
	 * @param privateKey
	 * @param publicKey
	 */
	public RSA(@Nullable byte[] privateKey, @Nullable byte[] publicKey) {
		super(ALGORITHM, SIGN_ALGORITHMS, getKey(ALGORITHM, privateKey, publicKey));
		this.signer = getSigner(ALGORITHM, SIGN_ALGORITHMS, privateKey, publicKey);
	}
	
	private static Key getKey(String algorithm, byte[] privateKey, byte[] publicKey){
		Assert.requiredArgument(privateKey == null && publicKey == null, "privateKey or publicKey");
		if(privateKey != null){
			return getPrivateKey(algorithm, privateKey);
		}else if(publicKey != null){
			return getPublicKey(algorithm, publicKey);
		}
		return null;
	}

	@Override
	public Signer<byte[], byte[]> getSigner() {
		return signer;
	}
}
