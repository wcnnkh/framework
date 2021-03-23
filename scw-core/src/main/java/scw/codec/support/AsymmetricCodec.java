package scw.codec.support;

import java.security.Key;

import javax.crypto.Cipher;

import scw.codec.DecodeException;
import scw.codec.EncodeException;
import scw.core.Assert;
import scw.io.UnsafeByteArrayOutputStream;
import scw.lang.Nullable;

/**
 * 非对称加密
 * 一次能加密的明文长度与密钥长度成正比：<br/>
 * len_in_byte(raw_data) = len_in_bit(key)/8 -11，如 1024bit
 * 的密钥，一次能加密的内容长度为 1024/8 -11 = 117 byte。<br/>
 * 所以非对称加密一般都用于加密对称加密算法的密钥，而不是直接加密内容。<br/>
 * @author shuchaowen
 *
 */
public class AsymmetricCodec extends CryptoCodec {
	/**
	 * ALGORITHM ['ælgərɪð(ə)m] 算法的意思
	 */
	public static final String RSA = "RSA";
	
	public static final String SHA1WithRSA = "SHA1WithRSA";
	
	protected final String algorithm;
	protected final Key encodeKey;
	private final Key decodeKey;
	private final int maxBlock;
	
	public AsymmetricCodec(String algorithm, @Nullable Key encodeKey, @Nullable Key decodeKey, int maxBlock) {
		Assert.requiredArgument(algorithm != null, "algorithm");
		Assert.requiredArgument(!(encodeKey == null && decodeKey == null), "encodeKey or decodeKey");
		this.algorithm = algorithm;
		this.encodeKey = encodeKey;
		this.decodeKey = decodeKey;
		this.maxBlock = maxBlock;
	}

	public final String getAlgorithm() {
		return algorithm;
	}

	public Key getEncodeKey() {
		return encodeKey;
	}

	public Key getDecodeKey() {
		return decodeKey;
	}

	public Cipher getCipher() {
		return getCipher(algorithm);
	}

	public byte[] encode(byte[] source) throws EncodeException {
		Assert.requiredArgument(source != null, "source");
		Cipher cipher = getCipher();
		int maxBlock = this.maxBlock - 11;
		@SuppressWarnings("resource")
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, encodeKey);
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
			throw new EncodeException("加密阀值为[" + maxBlock + "]的数据时发生异常", e);
		}
		return out.toByteArray();
	}

	public byte[] decode(byte[] source) throws DecodeException {
		Assert.requiredArgument(source != null, "source");
		int maxBlock = this.maxBlock;
		@SuppressWarnings("resource")
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			Cipher cipher = getCipher();
			cipher.init(Cipher.DECRYPT_MODE, decodeKey);
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
			throw new DecodeException("解密阀值为[" + maxBlock + "]的数据时发生异常", e);
		}
		return out.toByteArray();
	}

	@Override
	public String toString() {
		return algorithm;
	}
}
