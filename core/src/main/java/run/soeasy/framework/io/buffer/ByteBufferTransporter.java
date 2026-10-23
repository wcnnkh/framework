package run.soeasy.framework.io.buffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import lombok.NonNull;

/**
 * 二进制传输器接口，专门用于处理二进制数据的传输。
 *
 * <p>
 * 本接口扩展了 {@link BufferTransporter}，专注于二进制数据的转换和传输。
 * 所有传输方法按<b>目标类型</b>区分命名，每种操作语义唯一：
 *
 * <ul>
 * <li>{@code toByteArray} — 从源读取并经过传输器处理，输出字节数组</li>
 * <li>{@code transfer} — 底层传输（BufferReader→BufferWriter）及 BufferReader 便捷重载</li>
 * <li>{@code writeTo} — 将 {@link ByteBuffer} 写入目标</li>
 * <li>{@code readInto} — 将流式源读取并写入目标接收器</li>
 * </ul>
 *
 * <p>
 * <b>重要契约：</b>所有操作都会经过本传输器的处理逻辑，可能包含拷贝、编码、解码、加密、解压等字节变换，不一定原样转发；
 * 传入流/通道时会消费源并可能阻塞。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>基于 {@link ByteBuffer} 进行高效的二进制块传输</li>
 * <li>无缝集成 Java 标准 I/O 类（{@link InputStream}, {@link OutputStream}）</li>
 * <li>支持 NIO 通道（{@link ReadableByteChannel}, {@link WritableByteChannel}）</li>
 * <li>支持资源自动管理（try-with-resources）</li>
 * </ul>
 *
 * <p>
 * <b>使用示例：</b>
 *
 * <pre>{@code
 * ByteBufferTransporter transporter = ByteBufferTransporter.of(someBufferTransporter);
 *
 * // 读取文件内容到字节数组
 * byte[] data = transporter.toByteArray(path);
 *
 * // 将字节缓冲区写入文件
 * transporter.writeTo(byteBuffer, Files.newOutputStream(path));
 * }</pre>
 *
 * @author soeasy.run
 */
@FunctionalInterface
public interface ByteBufferTransporter extends BufferTransporter<ByteBuffer> {
	// ==================== 1. 工厂 ====================
	/**
	 * 将任意 {@link BufferTransporter} 包装为 {@link ByteBufferTransporter}。
	 *
	 * @param bufferTransporter 待包装的传输器
	 * @param <B>               ByteBuffer 的子类型
	 * @return ByteBufferTransporter 实例
	 */
	static <B extends ByteBuffer> ByteBufferTransporter of(@NonNull BufferTransporter<B> bufferTransporter) {
		if (bufferTransporter instanceof ByteBufferTransporter) {
			return (ByteBufferTransporter) bufferTransporter;
		}
		return new SimpleByteBufferTransporter<>(bufferTransporter);
	}

	/**
	 * 从 {@link BufferReader} 读取所有字节，经过传输器处理后转换为字节数组。
	 *
	 * @param reader 字节缓冲区读取器
	 * @return 完整的字节数组内容
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E           读取过程抛出的异常
	 */
	default <E extends Throwable> byte[] toByteArray(@NonNull BufferReader<? super ByteBuffer, ? extends E> reader)
			throws IOException, E {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		transfer(reader, output);
		return output.toByteArray();
	}

	/**
	 * 将 {@link ByteBuffer} 经过传输器处理后转换为字节数组。
	 *
	 * <p>
	 * 并非直接获取 buffer 底层数组，会完整走本传输器的变换链路。
	 *
	 * @param byteBuffer 字节缓冲区
	 * @return 处理后的字节数组
	 * @throws IOException 如果发生 I/O 错误
	 */
	default byte[] toByteArray(@NonNull ByteBuffer byteBuffer) throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			writeTo(byteBuffer, output);
			return output.toByteArray();
		}
	}

	/**
	 * 从 {@link InputStream} 读取所有字节，经过传输器处理后转换为字节数组。
	 *
	 * @param inputStream 输入流（会被消费）
	 * @return 完整的字节数组内容
	 * @throws IOException 如果发生 I/O 错误
	 */
	default byte[] toByteArray(@NonNull InputStream inputStream) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		readInto(inputStream, output);
		return output.toByteArray();
	}

	/**
	 * 从 {@link ReadableByteChannel} 读取所有字节，经过传输器处理后转换为字节数组。
	 *
	 * @param readableByteChannel 可读字节通道（会被消费）
	 * @return 完整的字节数组内容
	 * @throws IOException 如果发生 I/O 错误
	 */
	default byte[] toByteArray(@NonNull ReadableByteChannel readableByteChannel) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		readInto(readableByteChannel, output);
		return output.toByteArray();
	}

	/**
	 * 从 {@link BufferReader} 传输字节到 {@link OutputStream}。
	 *
	 * @param reader 字节缓冲区读取器
	 * @param output 输出流
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E           读取过程抛出的异常
	 */
	default <E extends Throwable> void transfer(@NonNull BufferReader<? super ByteBuffer, ? extends E> reader,
			@NonNull OutputStream output) throws IOException, E {
		transfer(reader, Channels.newChannel(output));
	}

	/**
	 * 从 {@link BufferReader} 传输字节到 {@link Path}。
	 *
	 * @param reader 字节缓冲区读取器
	 * @param target 目标路径
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E           读取过程抛出的异常
	 */
	default <E extends Throwable> void transfer(@NonNull BufferReader<? super ByteBuffer, ? extends E> reader,
			@NonNull Path target) throws IOException, E {
		try (WritableByteChannel writable = Files.newByteChannel(target, StandardOpenOption.WRITE)) {
			transfer(reader, writable);
		}
	}

	/**
	 * 从 {@link BufferReader} 传输字节到 {@link WritableByteChannel}。
	 *
	 * @param reader   字节缓冲区读取器
	 * @param writable 可写字节通道
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E           读取过程抛出的异常
	 */
	default <E extends Throwable> void transfer(@NonNull BufferReader<? super ByteBuffer, ? extends E> reader,
			@NonNull WritableByteChannel writable) throws IOException, E {
		transfer(reader, (b) -> writable.write(b));
	}

	/**
	 * 将 {@link ByteBuffer} 经过传输器处理后写入 {@link BufferWriter}。
	 *
	 * @param source 字节缓冲区
	 * @param writer 字节缓冲区写入器
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E           写入过程抛出的异常
	 */
	default <E extends Throwable> void writeTo(@NonNull ByteBuffer source,
			@NonNull BufferWriter<? super ByteBuffer, ? extends E> writer) throws IOException, E {
		try (InputStream input = new ByteBufferInputStream(source)) {
			readInto(input, writer);
		}
	}

	/**
	 * 将 {@link ByteBuffer} 经过传输器处理后写入 {@link OutputStream}。
	 *
	 * @param source 字节缓冲区
	 * @param output 输出流
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void writeTo(@NonNull ByteBuffer source, @NonNull OutputStream output) throws IOException {
		try (InputStream input = new ByteBufferInputStream(source)) {
			readInto(input, output);
		}
	}

	/**
	 * 将 {@link ByteBuffer} 经过传输器处理后写入 {@link WritableByteChannel}。
	 *
	 * @param source              字节缓冲区
	 * @param writableByteChannel 可写字节通道
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void writeTo(@NonNull ByteBuffer source, @NonNull WritableByteChannel writableByteChannel)
			throws IOException {
		try (InputStream input = new ByteBufferInputStream(source)) {
			readInto(input, writableByteChannel);
		}
	}

	/**
	 * 从 {@link InputStream} 读取，经过传输器处理后写入 {@link BufferWriter}。
	 *
	 * @param input  输入流
	 * @param writer 字节缓冲区写入器
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E           读取过程抛出的异常
	 */
	default <E extends Throwable> void readInto(@NonNull InputStream input,
			@NonNull BufferWriter<? super ByteBuffer, ? extends E> writer) throws IOException, E {
		readInto(Channels.newChannel(input), writer);
	}

	/**
	 * 在 {@link InputStream} 和 {@link OutputStream} 之间传输字节，经过传输器处理。
	 *
	 * @param input  输入流
	 * @param output 输出流
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void readInto(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
		readInto(Channels.newChannel(input), Channels.newChannel(output));
	}

	/**
	 * 从 {@link InputStream} 读取，经过传输器处理后写入 {@link WritableByteChannel}。
	 *
	 * @param input    输入流
	 * @param writable 可写字节通道
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void readInto(@NonNull InputStream input, @NonNull WritableByteChannel writable) throws IOException {
		readInto(Channels.newChannel(input), writable);
	}

	/**
	 * 从 {@link ReadableByteChannel} 读取，经过传输器处理后写入 {@link BufferWriter}。
	 *
	 * <p>
	 * 将通道包装为 BufferReader，交给底层 transfer 处理。
	 *
	 * @param readable 可读字节通道
	 * @param writer   字节缓冲区写入器
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E           读取过程抛出的异常
	 */
	default <E extends Throwable> void readInto(@NonNull ReadableByteChannel readable,
			@NonNull BufferWriter<? super ByteBuffer, ? extends E> writer) throws IOException, E {
		transfer((b) -> readable.read(b), writer);
	}

	/**
	 * 从 {@link ReadableByteChannel} 读取，经过传输器处理后写入 {@link OutputStream}。
	 *
	 * @param readable 可读字节通道
	 * @param output   输出流
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void readInto(@NonNull ReadableByteChannel readable, @NonNull OutputStream output) throws IOException {
		readInto(readable, Channels.newChannel(output));
	}

	/**
	 * 在两个 NIO 通道之间传输字节，经过传输器处理。
	 *
	 * @param readable 可读字节通道
	 * @param writable 可写字节通道
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void readInto(@NonNull ReadableByteChannel readable, @NonNull WritableByteChannel writable)
			throws IOException {
		transfer(readable::read, writable::write);
	}
}
