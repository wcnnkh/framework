package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 默认的二进制传输器实现。
 *
 * <p>
 * 本实现基于 {@link BufferTransporter}，使用 {@link ByteBuffer} 作为缓冲区，
 * 专门用于二进制数据（如文件、网络流）的传输。
 *
 * <p>
 * <b>使用示例：</b>
 * 
 * <pre>{@code
 * // 1. 创建有界池（控制内存占用）
 * BinaryTransferrer transferrer = new DefaultBinaryTransferrer(8192, 16);
 *
 * // 2. 创建无界池（追求极限性能）
 * BinaryTransferrer fastTransferrer = new DefaultBinaryTransferrer(8192);
 *
 * // 3. 执行传输
 * transferrer.transfer(reader, writer);
 * }</pre>
 *
 * <p>
 * <b>注意：</b>默认使用堆内内存。如需直接内存（零拷贝友好）， 请使用 {@link ByteBuffer#allocateDirect(int)}。
 *
 * @see BufferTransporter
 * @see ByteBuffer
 */
@RequiredArgsConstructor
@Getter
public class DefaultByteBufferTransporter implements ByteBufferTransporter {
	/**
	 * 底层的缓冲区传输器，使用 ByteBuffer 作为载体。
	 */
	@NonNull
	private final BufferTransporter<ByteBuffer> bufferTransferrer;

	/**
	 * 创建一个使用无界缓冲区池的二进制传输器（默认使用堆内存）。
	 *
	 * @param bufferCapacity 每个缓冲区的容量（字节）
	 */
	public DefaultByteBufferTransporter(int bufferCapacity) {
		this(new DefaultBufferTransporter<>(bufferCapacity, ByteBuffer::allocate));
	}

	/**
	 * 执行二进制数据传输。
	 *
	 * <p>
	 * 本方法将传输逻辑完全委托给底层的 {@link BufferTransporter}。 行为遵循阻塞 IO 语义：直到流结束才会返回。
	 *
	 * @param reader 字节缓冲区读取器
	 * @param writer 字节缓冲区写入器
	 * @throws IOException 如果发生 I/O 错误
	 */
	@Override
	public <R extends Throwable, W extends Throwable> void transfer(
			@NonNull BufferReader<? super ByteBuffer, ? extends R> reader,
			@NonNull BufferWriter<? super ByteBuffer, ? extends W> writer) throws IOException, R, W {
		bufferTransferrer.transfer(reader, writer);
	}
}