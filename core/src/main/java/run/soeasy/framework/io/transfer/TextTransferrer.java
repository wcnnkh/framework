package run.soeasy.framework.io.transfer;

import lombok.NonNull;
import run.soeasy.framework.io.source.ReaderSource;
import run.soeasy.framework.io.source.WriterSource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * 字符传输器接口，专门用于处理字符数据的传输。
 *
 * <p>本接口扩展了 {@link BufferTransferrer}，专注于字符数据的转换和传输，
 * 提供了从各种字符源到目标的便捷方法。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>基于 {@link CharBuffer} 进行高效的字符块传输</li>
 *   <li>无缝集成 Java 标准 I/O 类（{@link Readable}, {@link Appendable}, {@link Reader}, {@link Writer}）</li>
 *   <li>支持资源自动管理（try-with-resources）</li>
 * </ul>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * CharTransferrer transferrer = new DefaultCharTransferrer(1024);
 *
 * // 读取文件内容到字符串
 * String content = transferrer.readAll(Files.newBufferedReader(path));
 *
 * // 将字符串写入文件
 * transferrer.transfer(content, Files.newBufferedWriter(path));
 * }</pre>
 *
 * @author soeasy.run
 */
public interface TextTransferrer extends BufferTransferrer<CharBuffer> {

    /**
     * 从 {@link BufferReader} 读取所有字符并转换为字符串。
     *
     * @param reader 字符缓冲区读取器
     * @return 完整的字符串内容
     * @throws IOException 如果发生 I/O 错误
     */
    default String readToString(
            @NonNull BufferReader<? super CharBuffer> reader
    ) throws IOException {
        StringBuilder sb = new StringBuilder();
        writeTo(reader, sb);
        return sb.toString();
    }

    /**
     * 从 {@link Readable} 读取所有字符并转换为字符串。
     *
     * @param readable 可读字符源
     * @return 完整的字符串内容
     * @throws IOException 如果发生 I/O 错误
     */
    default String readToString(@NonNull Readable readable) throws IOException {
        StringBuilder sb = new StringBuilder();
        transfer(readable, sb);
        return sb.toString();
    }

    /**
     * 从 {@link ReaderSource} 读取所有字符并转换为字符串。
     *
     * @param readerSource 读者源
     * @return 完整的字符串内容
     * @throws IOException 如果发生 I/O 错误
     */
    default <I extends Reader> String readToString(@NonNull ReaderSource<I> readerSource) throws IOException {
        StringBuilder sb = new StringBuilder();
        transfer(readerSource, sb);
        return sb.toString();
    }

    /**
     * 将 {@link CharSequence} 转换为字符串。
     *
     * <p>通过 {@link CharBuffer} 包装字符序列并完全读取。
     *
     * @param charSequence 字符序列
     * @return 转换后的字符串
     */
    default String toString(@NonNull CharSequence charSequence) {
        try {
            return readToString(CharBuffer.wrap(charSequence));
        } catch (IOException e) {
            // CharBuffer.wrap 不会抛出 IOException，但为了接口兼容性保留
            throw new IllegalStateException("Internal error during character conversion", e);
        }
    }

    /**
     * 将字符从 {@link Readable} 传输到 {@link Appendable}。
     *
     * @param readable   可读字符源
     * @param appendable 可追加字符的目标
     * @throws IOException 如果发生 I/O 错误
     */
    default void transfer(
            @NonNull Readable readable,
            @NonNull Appendable appendable
    ) throws IOException {
        transfer(readable::read, appendable::append);
    }

    /**
     * 将字符从 {@link ReaderSource} 传输到 {@link Appendable}。
     *
     * @param readerSource 读者源
     * @param appendable   可追加字符的目标
     * @throws IOException 如果发生 I/O 错误
     */
    default <I extends Reader> void transfer(@NonNull ReaderSource<I> readerSource, @NonNull Appendable appendable) throws IOException {
        try (Reader reader = readerSource.getReader()) {
            transfer(reader, appendable);
        }
    }

    /**
     * 将字符从 {@link ReaderSource} 传输到 {@link WriterSource}。
     *
     * @param readerSource 读者源
     * @param writerSource 写者源
     * @throws IOException 如果发生 I/O 错误
     */
    default <I extends Reader, O extends Writer> void transfer(@NonNull ReaderSource<I> readerSource, @NonNull WriterSource<O> writerSource) throws IOException {
        try (Reader reader = readerSource.getReader();
             Writer writer = writerSource.getWriter()) {
            transfer(reader, writer);
        }
    }

    /**
     * 从 {@link Readable} 读取字符并使用 {@link BufferWriter} 写入。
     *
     * @param readable 可读字符源
     * @param writer   字符缓冲区写入器
     * @throws IOException 如果发生 I/O 错误
     */
    default void readFrom(
            @NonNull Readable readable,
            @NonNull BufferWriter<? super CharBuffer> writer
    ) throws IOException {
        transfer(readable::read, writer);
    }

    /**
     * 从 {@link ReaderSource} 读取字符并使用 {@link BufferWriter} 写入。
     *
     * @param readerSource 读者源
     * @param writer       字符缓冲区写入器
     * @throws IOException 如果发生 I/O 错误
     */
    default <I extends Reader> void readFrom(@NonNull ReaderSource<I> readerSource, @NonNull BufferWriter<? super CharBuffer> writer) throws IOException {
        try (Reader reader = readerSource.getReader()) {
            readFrom(reader, writer);
        }
    }

    /**
     * 从 {@link BufferReader} 读取字符并写入到 {@link Appendable}。
     *
     * @param reader     字符缓冲区读取器
     * @param appendable 可追加字符的目标
     * @throws IOException 如果发生 I/O 错误
     */
    default void writeTo(
            @NonNull BufferReader<? super CharBuffer> reader,
            @NonNull Appendable appendable
    ) throws IOException {
        transfer(reader, appendable::append);
    }

    /**
     * 从 {@link BufferReader} 读取字符并写入到 {@link WriterSource}。
     *
     * @param reader      字符缓冲区读取器
     * @param writerSource 写者源
     * @throws IOException 如果发生 I/O 错误
     */
    default <O extends Writer> void writeTo(@NonNull BufferReader<? super CharBuffer> reader, @NonNull WriterSource<O> writerSource) throws IOException {
        try (Writer writer = writerSource.getWriter()) {
            writeTo(reader, writer);
        }
    }
}