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
 * <li>{@code transfer} — 底层传输（BufferReader/Reader →
 * Appendable/Writer/BufferWriter）</li>
 * <li>{@code readInto} — 从 {@link Readable} 读取并写入目标</li>
 * <li>{@code writeTo} — 将 {@link CharSequence} 写入目标</li>
 * <li>{@code toString} / {@code readToString} — 从源读取并转换为 String</li>
 * </ul>
 *
 * <p>
 * <b>重载决议说明：</b>当参数类型存在继承关系时（如 {@link Writer} 继承 {@link Appendable}）， Java
 * 编译器依据"最具体方法"规则自动选择匹配最具体的重载，不会产生歧义。
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
 * // 从 Readable 读取到 Appendable
 * transporter.readInto(readable, stringBuilder);
 *
 * // 将 CharSequence 写入 Writer
 * transporter.writeTo("hello", writer);
 *
 * // 读取为 String
 * String content = transporter.toString(readerSource);
 * }</pre>
 *
 * @author soeasy.run
 */
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
	 * 从 {@link BufferReader} 读取所有字符并转换为字符串。
	 *
	 * @param reader 字符缓冲区读取器
	 * @return 完整的字符串内容
	 * @throws IOException 如果发生 I/O 错误
	 */
	default <E extends Throwable> String readToString(@NonNull BufferReader<? super CharBuffer, ? extends E> reader)
			throws IOException, E {
		StringBuilder appendable = new StringBuilder();
		transfer(reader, appendable);
		return appendable.toString();
	}

	/**
	 * 从 {@link Reader} 读取所有字符并转换为字符串。
	 *
	 * @param reader Reader 源
	 * @return 完整的字符串内容
	 * @throws IOException 如果发生 I/O 错误
	 */
	default String readToString(@NonNull Reader reader) throws IOException {
		StringBuilder appendable = new StringBuilder();
		transfer(reader, (b) -> appendable.append(b));
		return appendable.toString();
	}

	/**
	 * 将 {@link CharSequence} 转换为字符串。
	 *
	 * @param charSequence 字符序列
	 * @return 转换后的字符串
	 */
	default String readToString(@NonNull CharBuffer source) throws IOException {
		StringBuilder appendable = new StringBuilder();
		transfer(source, (b) -> appendable.append(b));
		return appendable.toString();
	}

	default <R extends Throwable> void transfer(@NonNull BufferReader<? super CharBuffer, ? extends R> reader,
			@NonNull Appendable appendable) throws IOException, R {
		transfer(reader, (b) -> appendable.append(b));
	}

	/**
	 * 从 {@link BufferReader} 读取字符并写入 {@link Writer}。
	 *
	 * @param reader 字符缓冲区读取器
	 * @param writer 字符写入器
	 * @throws IOException 如果发生 I/O 错误
	 */
	default <R extends Throwable> void transfer(@NonNull BufferReader<? super CharBuffer, ? extends R> reader,
			@NonNull Writer writer) throws IOException, R {
		transfer(reader, (b) -> writer.append(b));
	}

	/**
	 * 将 {@link CharSequence} 写入 {@link BufferWriter}。
	 *
	 * @param charSequence 字符序列
	 * @param writer       字符缓冲区写入器
	 * @throws IOException 如果发生 I/O 错误
	 */
	default <E extends Throwable> void transfer(@NonNull CharBuffer buffer,
			@NonNull BufferWriter<? super CharBuffer, ? extends E> writer) throws IOException, E {
		transfer((b) -> buffer.read(b), writer);
	}

	/**
	 * 将 {@link CharSequence} 写入 {@link Writer}。
	 *
	 * @param charSequence 字符序列
	 * @param writer       字符写入器
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void transfer(@NonNull CharBuffer buffer, @NonNull Writer writer) throws IOException {
		transfer((b) -> buffer.read(b), writer);
	}

	/**
	 * 从 {@link Reader} 读取字符并写入 {@link BufferWriter}。
	 *
	 * @param reader 字符缓冲区读取器
	 * @param writer 字符缓冲区写入器
	 * @throws IOException 如果发生 I/O 错误
	 */
	default <W extends Throwable> void transfer(@NonNull Reader reader,
			@NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, W {
		transfer((s) -> reader.read(s), writer);
	}

	/**
	 * 从 {@link Reader} 读取字符并直接写入 {@link Writer}。
	 *
	 * @param reader Reader 源
	 * @param writer Writer 目标
	 * @throws IOException 如果发生 I/O 错误
	 */
	default void transfer(@NonNull Reader reader, @NonNull Writer writer) throws IOException {
		transfer((s) -> reader.read(s), writer);
	}
}