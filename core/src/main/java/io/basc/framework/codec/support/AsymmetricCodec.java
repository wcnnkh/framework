package io.basc.framework.codec.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.Cipher;

import io.basc.framework.codec.CodecException;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.BufferProcessor;
import io.basc.framework.io.IOUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

/**
 * 非对称加密 一次能加密的明文长度与密钥长度成正比：<br/>
 * len_in_byte(raw_data) = len_in_bit(key)/8 -11，如 1024bit 的密钥，一次能加密的内容长度为
 * 1024/8 -11 = 117 byte。<br/>
 * 所以非对称加密一般都用于加密对称加密算法的密钥，而不是直接加密内容。<br/>
 * 
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

	@Override
	public String toString() {
		return algorithm;
	}

	public <E extends Throwable> void doFinal(Cipher cipher, int maxBlock, InputStream source,
			BufferProcessor<byte[], E> targetProcessor) throws Throwable {
		Assert.requiredArgument(source != null, "source");
		IOUtils.read(source, maxBlock, (buff, offset, len) -> {
			byte[] target = cipher.doFinal(buff, offset, len);
			targetProcessor.process(target, 0, target.length);
		});
	}

	@Override
	public <E extends Throwable> void encode(InputStream source, int bufferSize,
			BufferProcessor<byte[], E> targetProcessor) throws IOException, EncodeException, E {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, encodeKey);
		} catch (InvalidKeyException e) {
			throw new CodecException(e);
		}

		try {
			doFinal(cipher, this.maxBlock - 11, source, targetProcessor);
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			throw new EncodeException(e);
		}
	}

	@Override
	public <E extends Throwable> void decode(InputStream source, int bufferSize,
			BufferProcessor<byte[], E> targetProcessor) throws DecodeException, IOException, E {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.DECRYPT_MODE, decodeKey);
		} catch (InvalidKeyException e) {
			throw new CodecException(e);
		}

		try {
			doFinal(cipher, this.maxBlock, source, targetProcessor);
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			throw new EncodeException(e);
		}
	}

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		encode(source, bufferSize, target::write);
	}

	@Override
	public void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException {
		decode(source, bufferSize, target::write);
	}
}
