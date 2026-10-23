package run.soeasy.framework.codec.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.io.BinaryCoder;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.BufferWriter;
import run.soeasy.framework.io.buffer.ByteBufferTransporter;

/**
 * 算法传输器抽象类，实现{@link ByteBufferTransporter}接口，基于算法工厂{@link AlgorithmFactory}提供的算法实例，
 * 处理与安全相关的二进制数据传输（如加密、解密等）。
 *
 * <p>
 * 该类作为安全算法与二进制传输的桥梁，通过算法工厂管理算法实例的创建与配置， 子类需实现具体的算法传输逻辑（如加密传输、解密传输）。
 *
 * @param <T> 算法类型（如Cipher、MessageDigest等安全算法对象）
 * @author soeasy.run
 * @see ByteBufferTransporter
 * @see AlgorithmFactory
 */
@Getter
@RequiredArgsConstructor
public abstract class AlgorithmCoder<T> implements BinaryCoder {

	/**
	 * 算法工厂，用于创建和配置算法实例（如初始化加密/解密算法）
	 */
	@NonNull
	private final AlgorithmFactory<? extends T> algorithmFactory;

	/**
	 * 构造算法传输器，支持通过初始化器配置算法实例
	 *
	 * @param algorithmFactory     基础算法工厂，提供原始算法实例
	 * @param algorithmInitializer 算法初始化器，用于对算法实例进行额外配置（如设置密钥、模式等）
	 */
	public AlgorithmCoder(AlgorithmFactory<? extends T> algorithmFactory,
			@NonNull ThrowingConsumer<? super T, ? extends GeneralSecurityException> algorithmInitializer) {
		this(new ConfigurableAlgorithmFactory<>(algorithmFactory, algorithmInitializer));
	}

	@Override
	public <R extends Throwable, W extends Throwable> void transfer(
			@NonNull BufferReader<? super ByteBuffer, ? extends R> reader,
			@NonNull BufferWriter<? super ByteBuffer, ? extends W> writer) throws IOException, R, W {
		try {
			T algorithm = this.algorithmFactory.getAlgorithm();
			transfer(algorithm, reader, writer);
		} catch (GeneralSecurityException e) {
			throw new IOException(getClass().getSimpleName(), e);
		}
	}

	public abstract <R extends Throwable, W extends Throwable> void transfer(T algorithm,
			@NonNull BufferReader<? super ByteBuffer, ? extends R> reader,
			@NonNull BufferWriter<? super ByteBuffer, ? extends W> writer)
			throws IOException, R, W, GeneralSecurityException;
}