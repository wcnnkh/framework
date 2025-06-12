package run.soeasy.framework.codec.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

import lombok.NonNull;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.io.BufferConsumer;

/**
 * 非对称加密 一次能加密的明文长度与密钥长度成正比： len_in_byte(raw_data) = len_in_bit(key)/8 -11，如
 * 1024bit 的密钥，一次能加密的内容长度为 1024/8 -11 = 117 byte。
 * 所以非对称加密一般都用于加密对称加密算法的密钥，而不是直接加密内容。
 * 
 * @author wcnnkh
 *
 */
public class AsymmetricCodec extends CryptoCodec {
	public static final String SHA1WithRSA = "SHA1WithRSA";

	private final int maxBlock;

	public AsymmetricCodec(@NonNull String algorithm, Key encodeKey, Key decodeKey, int maxBlock) {
		this(algorithm, null, encodeKey == null ? null : (c, o) -> c.init(o, encodeKey),
				decodeKey == null ? null : (c, o) -> c.init(o, decodeKey), maxBlock);
	}

	public AsymmetricCodec(@NonNull String transformation, Object provider, CipherInitializer encodeCipherInitializer,
			CipherInitializer decodeCipherInitializer, int maxBlock) {
		super(transformation, provider, encodeCipherInitializer, decodeCipherInitializer);
		this.maxBlock = maxBlock;
	}

	public AsymmetricCodec(CipherFactory encoder, CipherFactory decoder, int maxBlock) {
		super(encoder, decoder);
		this.maxBlock = maxBlock;
	}

	@Override
	public <E extends Throwable> void encode(InputStream source, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> targetConsumer) throws IOException, EncodeException, E {
		super.encode(source, this.maxBlock - 11, targetConsumer);
	}

	@Override
	public <E extends Throwable> void decode(InputStream source, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> targetConsumer) throws DecodeException, IOException, E {
		super.decode(source, this.maxBlock, targetConsumer);
	}
}
