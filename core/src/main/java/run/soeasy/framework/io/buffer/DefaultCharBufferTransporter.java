package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 默认的字符串传输器实现。
 *
 * <p>
 * 本实现基于 {@link BufferTransporter}，使用 {@link CharBuffer} 作为缓冲区， 专门用于字符数据的传输。
 *
 * @see BufferTransporter
 * @see CharBuffer
 */
@RequiredArgsConstructor
@Getter
public class DefaultCharBufferTransporter implements CharBufferTransporter {
	/**
	 * 底层的缓冲区传输器，使用 CharBuffer 作为载体。
	 */
	@NonNull
	private final BufferTransporter<CharBuffer> bufferTransferrer;

	/**
	 * 创建一个使用无界缓冲区池的字符串传输器。
	 *
	 * @param bufferCapacity 每个字符缓冲区的容量
	 */
	public DefaultCharBufferTransporter(int bufferCapacity) {
		this(new DefaultBufferTransporter<>(bufferCapacity, CharBuffer::allocate));
	}

	/**
	 * 执行字符串数据传输。
	 *
	 * <p>
	 * 本方法将传输逻辑完全委托给底层的 {@link BufferTransporter}。
	 *
	 * @param reader 字符缓冲区读取器
	 * @param writer 字符缓冲区写入器
	 * @throws IOException 如果发生 I/O 错误
	 */
	@Override
	public <R extends Throwable, W extends Throwable> void transfer(
			@NonNull BufferReader<? super CharBuffer, ? extends R> reader,
			@NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, R, W {
		bufferTransferrer.transfer(reader, writer);
	}
}