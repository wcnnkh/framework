package run.soeasy.framework.codec.crypto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.codec.security.AlgorithmFactory;
import run.soeasy.framework.codec.security.AlgorithmCoder;
import run.soeasy.framework.core.NestedRuntimeException;
import run.soeasy.framework.io.Binary;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.BufferWriter;

/**
 * 基于Cipher的算法传输器，继承自{@link AlgorithmCoder}，专门用于通过{@link Cipher}实现加密或解密的流式传输处理，
 * 支持分块处理模式与流式处理模式，适配对称加密、非对称加密等各类基于Cipher的加解密场景。
 * 
 * <p>
 * 该类通过配置{@link #chunkSize}（分块大小）控制数据处理方式： 1.
 * 当chunkSize&gt;0时，按指定分块大小读取数据并使用{@link Cipher#doFinal(byte[], int, int)}处理每个分块
 * 2.
 * 当chunkSize≤0时，使用{@link Cipher#update(byte[], int, int)}处理流式数据，最后通过{@link Cipher#doFinal()}处理收尾数据
 * 
 * @author soeasy.run
 * @see AlgorithmCoder
 * @see Cipher
 * @see javax.crypto.Cipher
 */
@Getter
@Setter
public class CipherCoder extends AlgorithmCoder<Cipher> {

	/**
	 * 分块处理大小（字节），用于控制数据分块加密/解密的粒度，大于0时启用分块模式
	 */
	private int chunkSize;

	/**
	 * 构造Cipher传输器（指定Cipher算法工厂）
	 * 
	 * @param algorithmFactory Cipher算法工厂，用于创建并初始化{@link Cipher}实例（如设置加密模式、密钥等）
	 */
	public CipherCoder(@NonNull AlgorithmFactory<? extends Cipher> algorithmFactory) {
		super(algorithmFactory);
	}

	@Override
	public <R extends Throwable, W extends Throwable> void transfer(Cipher algorithm,
			@NonNull BufferReader<? super ByteBuffer, ? extends R> reader,
			@NonNull BufferWriter<? super ByteBuffer, ? extends W> writer)
			throws IOException, R, W, GeneralSecurityException {
		Binary binary = chunkSize > 0 ? new Binary(chunkSize) : IOUtils.binary();
		ByteBuffer outputBuffer = ByteBuffer
				.allocate(algorithm.getOutputSize(chunkSize > 0 ? chunkSize : Binary.DEFAULT_BYTE_BUFFER_SIZE));
		try {
			binary.transfer(reader, (inputBuffer) -> {
				try {
					outputBuffer.clear();
					int size = chunkSize > 0 ? algorithm.doFinal(inputBuffer, outputBuffer)
							: algorithm.update(inputBuffer, outputBuffer);
					if (size > 0) {
						writer.write(outputBuffer);
					}
				} catch (GeneralSecurityException e) {
					throw new NestedRuntimeException(e);
				}
			});
		} catch (NestedRuntimeException e) {
			Throwable root = e.getCause();
			if (root instanceof GeneralSecurityException) {
				throw (GeneralSecurityException) root;
			} else {
				throw e;
			}
		}

		if (chunkSize > 0) {
			byte[] block = algorithm.doFinal();
			if (block != null) {
				writer.write(ByteBuffer.wrap(block));
			}
		}
	}
}