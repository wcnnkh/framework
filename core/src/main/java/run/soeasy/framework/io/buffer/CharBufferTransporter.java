package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;

/**
 * 字符传输器接口，专门用于处理字符数据的传输。
 *
 * <p>
 * 本接口扩展了 {@link BufferTransporter}，专注于字符数据的转换和传输。
 * 所有传输方法按<b>目标类型</b>区分命名，每种操作语义唯一：
 *
 * <ul>
 * <li>{@code transfer} — 底层传输（BufferReader/Reader → Appendable/Writer/BufferWriter）</li>
 * <li>{@code readInto} — 从 {@link Reader} 读取并写入目标接收器</li>
 * <li>{@code writeTo} — 将 {@link CharBuffer} 写入目标接收器</li>
 * <li>{@code toCharSequence} — 从源读取，经过传输器处理，输出 CharSequence</li>
 * </ul>
 *
 * <p>
 * <b>重要契约：</b>所有操作都会经过本传输器的处理逻辑，可能包含字符变换；读取源时会消费源、可能阻塞；
 * 不会关闭外部传入的 Reader、Writer、Appendable。
 *
 * <p>
 * <b>重载决议说明：</b>当参数类型存在继承关系时（如 {@link Writer} 继承 {@link Appendable}），
 * Java 编译器依据"最具体方法"规则自动选择匹配最具体的重载，不会产生歧义。
 *
 * <p>
 * <b>使用示例：</b>
 *
 * <pre>{@code
 * CharBufferTransporter transporter = CharBufferTransporter.of(someBufferTransporter);
 *
 * // 从 Reader 传输到 Writer
 * transporter.transfer(reader, writer);
 *
 * // 从 Reader 读取到 Appendable
 * transporter.readInto(reader, stringBuilder);
 *
 * // 将 CharBuffer 写入 Writer
 * transporter.writeTo(charBuffer, writer);
 *
 * // 读取为 CharSequence
 * CharSequence content = transporter.toCharSequence(readerSource);
 * }</pre>
 *
 * @author soeasy.run
 */
@FunctionalInterface
public interface CharBufferTransporter extends BufferTransporter<CharBuffer> {
	/**
	 * 将任意 {@link BufferTransporter} 包装为 {@link CharBufferTransporter}。
	 *
	 * @param bufferTransporter 待包装的传输器
	 * @param <B>               CharBuffer 的子类型
	 * @return CharBufferTransporter 实例
	 */
	static <B extends CharBuffer> CharBufferTransporter of(@NonNull BufferTransporter<B> bufferTransporter) {
		if (bufferTransporter instanceof CharBufferTransporter) {
			return (CharBufferTransporter) bufferTransporter;
		}
		return new SimpleCharBufferTransporter<>(bufferTransporter);
	}

	/**
	 * 从 {@link BufferReader} 读取所有字符，经过传输器处理后转换为 CharSequence。
	 *
	 * @param reader 字符缓冲区读取器
	 * @return 完整的字符序列
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E 读取过程抛出的异常
	 */
	default <E extends Throwable> CharSequence toCharSequence(@NonNull BufferReader<? super CharBuffer, ? extends E> reader)
			throws IOException, E {
		StringBuilder appendable = new StringBuilder();
		transfer(reader, appendable::append);
		return appendable.toString();
	}

	/**
	 * 从 {@link Reader} 读取所有字符，经过传输器处理后转换为 CharSequence。
	 *
	 * @param reader Reader 源
	 * @return 完整的字符序列
	 * @throws IOException 如果发生 I/O 错误
	 */
	default CharSequence toCharSequence(@NonNull Reader reader) throws IOException {
		StringBuilder appendable = new StringBuilder();
		readInto(reader, appendable::append);
		return appendable.toString();
	}

	/**
	 * 将 {@link CharBuffer} 经过传输器处理后转换为 CharSequence。
	 *
	 * @param source 字符缓冲区
	 * @return 处理后的字符序列
	 * @throws IOException 如果发生 I/O 错误
	 */
	default CharSequence toCharSequence(@NonNull CharBuffer source) throws IOException {
		StringBuilder appendable = new StringBuilder();
		writeTo(source, appendable::append);
		return appendable.toString();
	}

	/**
	 * 从 {@link BufferReader} 读取字符并写入 {@link Writer}。
	 *
	 * @param reader 字符缓冲区读取器
	 * @param writer 字符写入器
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E 读取过程抛出的异常
	 */
	default <E extends Throwable> void transfer(@NonNull BufferReader<? super CharBuffer, ? extends E> reader,
			@NonNull Writer writer) throws IOException, E {
		transfer(reader, (b) -> writer.append(b));
	}

	/**
	 * 将 {@link CharBuffer} 经过传输器处理后写入 {@link BufferWriter}。
	 *
	 * @param buffer 字符缓冲区
	 * @param writer 字符缓冲区写入器
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E 写入过程抛出的异常
	 */
	default <E extends Throwable> void writeTo(@NonNull CharBuffer buffer,
			@NonNull BufferWriter<? super CharBuffer, ? extends E> writer) throws IOException, E {
		transfer((b) -> buffer.read(b), writer);
	}

	/**
	 * 将 {@link CharBuffer} 经过传输器处理后写入 {@link Writer}。
	 *
	 * @param buffer 字符缓冲区
	 * @param writer 字符写入器
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void writeTo(@NonNull CharBuffer buffer, @NonNull Writer writer) throws IOException {
		transfer((b) -> buffer.read(b), writer);
	}

	/**
	 * 从 {@link Reader} 读取字符，经过传输器处理后写入 {@link BufferWriter}。
	 *
	 * @param reader 字符输入源
	 * @param writer 字符缓冲区写入器
	 * @throws IOException 如果发生 I/O 错误
	 * @throws E 读取过程抛出的异常
	 */
	default <E extends Throwable> void readInto(@NonNull Reader reader,
			@NonNull BufferWriter<? super CharBuffer, ? extends E> writer) throws IOException, E {
		transfer((s) -> reader.read(s), writer);
	}

	/**
	 * 从 {@link Reader} 读取字符，经过传输器处理后写入 {@link Writer}。
	 *
	 * @param reader Reader 源
	 * @param writer Writer 目标
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void readInto(@NonNull Reader reader, @NonNull Writer writer) throws IOException {
		transfer((s) -> reader.read(s), writer);
	}
}
