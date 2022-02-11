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
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.UnsafeByteArrayOutputStream;
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

	public void doFinal(Cipher cipher, int maxBlock, InputStream source, OutputStream output)
			throws IOException, Exception {
		Assert.requiredArgument(source != null, "source");
		byte[] buffer = new byte[maxBlock];
		IOUtils.read(source, maxBlock, (buff, offset, len) -> {
			int resLen = cipher.doFinal(buff, offset, len, buffer);
			output.write(buffer, 0, resLen);
		});
	}

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target, int count)
			throws IOException, EncodeException {
		if (count == 1) {
			Cipher cipher = getCipher();
			try {
				cipher.init(Cipher.ENCRYPT_MODE, encodeKey);
			} catch (InvalidKeyException e) {
				throw new CodecException(e);
			}

			try {
				doFinal(cipher, this.maxBlock - 11, source, target);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new EncodeException(e);
			}
		}
		super.encode(source, bufferSize, target, count);
	}

	@Override
	public void decode(InputStream source, int bufferSize, OutputStream target, int count)
			throws DecodeException, IOException {
		if (count == 1) {
			Cipher cipher = getCipher();
			try {
				cipher.init(Cipher.DECRYPT_MODE, decodeKey);
			} catch (InvalidKeyException e) {
				throw new CodecException(e);
			}

			try {
				doFinal(cipher, this.maxBlock, source, target);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new EncodeException(e);
			}
		}
		super.decode(source, bufferSize, target, count);
	}

	@Override
	public byte[] encode(InputStream source, int bufferSize, int count) throws IOException, EncodeException {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, encodeKey);
		} catch (InvalidKeyException e) {
			throw new CodecException(e);
		}

		InputStream input = source;
		for (int i = 0; i < count; i++) {
			UnsafeByteArrayOutputStream output = new UnsafeByteArrayOutputStream();
			try {
				doFinal(cipher, this.maxBlock - 11, input, output);
			} catch (Exception e) {
				throw new DecodeException(e);
			}
			input = output.toInputStream();
		}
		return IOUtils.toByteArray(input);
	}

	@Override
	public byte[] decode(InputStream source, int bufferSize, int count) throws IOException, DecodeException {
		Cipher cipher = getCipher();
		try {
			cipher.init(Cipher.DECRYPT_MODE, decodeKey);
		} catch (InvalidKeyException e) {
			throw new CodecException(e);
		}

		InputStream input = source;
		for (int i = 0; i < count; i++) {
			UnsafeByteArrayOutputStream output = new UnsafeByteArrayOutputStream();
			try {
				doFinal(cipher, this.maxBlock, input, output);
			} catch (Exception e) {
				throw new DecodeException(e);
			}
			input = output.toInputStream();
		}
		return IOUtils.toByteArray(input);
	}
}
