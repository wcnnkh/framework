package scw.codec.support;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import scw.codec.Signer;
import scw.core.Assert;
import scw.lang.Nullable;

/**
 * 非对称编解码器
 * 
 * @author shuchaowen
 *
 */
public class AsymmetricCodec extends KeyCodec {
	protected final String signAlgorithm;
	private final Signer<byte[], byte[]> signer; 

	/**
	 * @param algorithm
	 *            算法
	 * @param signAlgorithm
	 *            签名算法
	 * @param key
	 */
	public AsymmetricCodec(String algorithm, String signAlgorithm, Key key) {
		super(algorithm, key);
		Assert.requiredArgument(signAlgorithm != null, "signAlgorithm");
		this.signAlgorithm = signAlgorithm;
		PrivateKey privateKey = key instanceof PrivateKey ? (PrivateKey) key : null;
		PublicKey publicKey = key instanceof PublicKey ? (PublicKey) key : null;
		this.signer = new AsymmetricSigner(signAlgorithm, privateKey, publicKey);
	}

	public final String getSignAlgorithm() {
		return signAlgorithm;
	}

	public Signer<byte[], byte[]> getSigner(@Nullable byte[] privateKey,
			@Nullable byte[] publicKey) {
		return getSigner(algorithm, signAlgorithm, privateKey, publicKey);
	}

	public Signer<byte[], byte[]> getSigner() {
		return signer;
	}
	
	public static Signer<byte[], byte[]> getSigner(String algorithm,
			String signAlgorithm, @Nullable byte[] privateKey,
			@Nullable byte[] publicKey) {
		PrivateKey privKey = privateKey == null ? null : getPrivateKey(
				algorithm, privateKey);
		PublicKey pubKey = publicKey == null ? null : getPublicKey(algorithm,
				publicKey);
		return new AsymmetricSigner(signAlgorithm, privKey, pubKey);
	}
}
