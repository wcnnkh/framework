package run.soeasy.framework.codec.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.security.AlgorithmFactory;
import run.soeasy.framework.codec.security.AlgorithmTransferrer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.IOUtils;

/**
 * 基于Cipher的算法传输器，继承自{@link AlgorithmTransferrer}，专门用于通过{@link Cipher}实现加密或解密的流式传输处理，
 * 支持分块处理模式与流式处理模式，适配对称加密、非对称加密等各类基于Cipher的加解密场景。
 * 
 * <p>
 * 该类通过配置{@link #chunkSize}（分块大小）控制数据处理方式： 1.
 * 当chunkSize&gt;0时，按指定分块大小读取数据并使用{@link Cipher#doFinal(byte[], int, int)}处理每个分块 2.
 * 当chunkSize≤0时，使用{@link Cipher#update(byte[], int, int)}处理流式数据，最后通过{@link Cipher#doFinal()}处理收尾数据
 * 
 * @author soeasy.run
 * @see AlgorithmTransferrer
 * @see Cipher
 * @see javax.crypto.Cipher
 */
@Getter
@Setter
public class CipherTransferrer extends AlgorithmTransferrer<Cipher> {

	/**
	 * 分块处理大小（字节），用于控制数据分块加密/解密的粒度，大于0时启用分块模式
	 */
	private int chunkSize;

	/**
	 * 构造Cipher传输器（指定Cipher算法工厂）
	 * 
	 * @param algorithmFactory Cipher算法工厂，用于创建并初始化{@link Cipher}实例（如设置加密模式、密钥等）
	 */
	public CipherTransferrer(@NonNull AlgorithmFactory<? extends Cipher> algorithmFactory) {
		super(algorithmFactory);
	}

	/**
	 * 基于Cipher实例执行数据的加密/解密传输（核心处理逻辑）
	 * 
	 * <p>
	 * 处理流程根据{@link #chunkSize}分为两种模式：
	 * <ul>
	 * <li><strong>分块模式（chunkSize&gt;0）</strong>：
	 * 按chunkSize读取输入流数据，对每个分块调用{@link Cipher#doFinal(byte[], int, int)}处理，
	 * 并将处理结果通过{@link BufferConsumer}输出</li>
	 * <li><strong>流式模式（chunkSize≤0）</strong>：
	 * 按bufferSize读取数据，通过{@link Cipher#update(byte[], int, int)}处理流式数据，
	 * 全部数据处理完成后调用{@link Cipher#doFinal()}处理剩余数据，并输出最终结果</li>
	 * </ul>
	 * 
	 * @param <E>        缓冲区消费者可能抛出的异常类型
	 * @param algorithm  Cipher实例（已初始化，用于加密或解密）
	 * @param source     待处理的输入流（原始数据或加密数据）
	 * @param bufferSize 流式读取的缓冲区大小（分块模式下忽略，使用chunkSize）
	 * @param target     接收处理后数据的缓冲区消费者
	 * @throws IOException              当输入流读取失败时抛出
	 * @throws E                        当缓冲区消费者处理数据时抛出
	 * @throws GeneralSecurityException 当Cipher算法执行失败时抛出（如加密/解密错误、数据不完整等）
	 */
	@Override
	public <E extends Throwable> void transferTo(Cipher algorithm, @NonNull InputStream source, int bufferSize,
			@NonNull BufferConsumer<? super byte[], ? extends E> target)
			throws IOException, E, GeneralSecurityException {
		if (this.chunkSize > 0) {
			// 分块模式：按chunkSize处理数据，每个分块调用doFinal
			IOUtils.transferTo(source, this.chunkSize, (buff, offset, len) -> {
				try {
					byte[] block = algorithm.doFinal(buff, offset, len);
					if (block != null) {
						target.accept(block, 0, block.length);
					}
				} catch (GeneralSecurityException e) {
					throw new CodecException(e);
				}
			});
		} else {
			// 流式模式：先通过update处理数据，最后用doFinal收尾
			IOUtils.transferTo(source, bufferSize, (buff, offset, len) -> {
				byte[] updated = algorithm.update(buff, offset, len);
				if (updated != null) {
					target.accept(updated, 0, updated.length);
				}
			});

			// 处理最终数据块（可能为null，取决于Cipher算法）
			byte[] endArray = algorithm.doFinal();
			if (endArray != null) {
				target.accept(endArray, 0, endArray.length);
			}
		}
	}
}