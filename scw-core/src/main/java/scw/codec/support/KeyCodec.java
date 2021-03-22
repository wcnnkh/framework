package scw.codec.support;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;

import scw.codec.DecodeException;
import scw.codec.EncodeException;
import scw.core.Assert;
import scw.io.UnsafeByteArrayOutputStream;

public class KeyCodec extends CryptoCodec {
	protected final String algorithm;
	protected final Key key;

	/**
	 * @param algorithm
	 *            算法
	 * @param key
	 */
	public KeyCodec(String algorithm, Key key) {
		Assert.requiredArgument(algorithm != null, "algorithm");
		Assert.requiredArgument(key != null, "key");
		this.algorithm = algorithm;
		this.key = key;
	}

	public final String getAlgorithm() {
		return algorithm;
	}

	public final Key getKey() {
		return key;
	}

	public Cipher getCipher() {
		return getCipher(algorithm);
	}

	public byte[] encode(byte[] source) throws EncodeException {
		Cipher cipher = getCipher();
		/**
		 * 一次能加密的明文长度与密钥长度成正比：<br/>
		 * len_in_byte(raw_data) = len_in_bit(key)/8 -11，如 1024bit
		 * 的密钥，一次能加密的内容长度为 1024/8 -11 = 117 byte。<br/>
		 * 所以非对称加密一般都用于加密对称加密算法的密钥，而不是直接加密内容。<br/>
		 */
		int maxBlock = key.getEncoded().length / 8 - 11; // 最大块
		@SuppressWarnings("resource")
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			while (source.length > offSet) {
				if (source.length - offSet > maxBlock) {
					// 可以调用以下的doFinal（）方法完成加密或解密数据：
					buff = cipher.doFinal(source, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(source, offSet, source.length
							- offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
		} catch (Exception e) {
			throw new EncodeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
		}
		return out.toByteArray();
	}

	public byte[] decode(byte[] source) throws DecodeException {
		int maxBlock = key.getEncoded().length / 8; // 最大块
		@SuppressWarnings("resource")
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			Cipher cipher = getCipher();
			cipher.init(Cipher.ENCRYPT_MODE, key);
			while (source.length > offSet) {
				if (source.length - offSet > maxBlock) {
					// 可以调用以下的doFinal（）方法完成加密或解密数据：
					buff = cipher.doFinal(source, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(source, offSet, source.length
							- offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
		} catch (Exception e) {
			throw new EncodeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
		}
		return out.toByteArray();
	}

	public PrivateKey getPrivateKey(byte[] privateKey)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return getPrivateKey(algorithm, privateKey);
	}

	public PublicKey getPublicKey(byte[] publicKey)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return getPublicKey(algorithm, publicKey);
	}

	@Override
	public String toString() {
		return algorithm;
	}

	public static KeyCodec getPrivateKeyCodec(String algorithm,
			byte[] privateKey) {
		return new KeyCodec(algorithm, getPrivateKey(algorithm, privateKey));
	}

	public static KeyCodec getPublicKeyCodec(String algorithm, byte[] publicKey) {
		return new KeyCodec(algorithm, getPrivateKey(algorithm, publicKey));
	}
}
