package run.soeasy.framework.io.source;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;

/**
 * 写者源接口，用于提供 {@link Writer} 实例。
 *
 * <p>
 * 这是一个函数式接口，将 {@link Writer} 的创建或获取逻辑进行封装与延迟。
 * 与 {@link ReaderSource} 对称，本接口专注于<b>输出方向</b>的资源抽象。
 *
 * <p>
 * <b>设计意图：</b>
 * <ul>
 *   <li>将 {@link Writer} 的获取逻辑与消费逻辑解耦，支持延迟打开和自动关闭</li>
 *   <li>提供从 {@link BufferReader}、{@link CharBuffer} 或 {@link ReaderSource} 写入数据的便捷方法</li>
 * </ul>
 *
 * <p>
 * <b>资源责任：</b> 所有 {@code write...()} 方法均通过 try-with-resources 确保 {@link Writer}
 * 被正确关闭；调用者无需（也不应）手动关闭传入的 {@code Writer} 实例。
 *
 * <p>
 * <b>多重实现说明：</b> 当一个类同时实现 {@link WriterSource} 和 {@link OutputStreamSource} 时，
 * 需通过方法名区分字符方向与字节方向的写入，例如 {@code writeCharBufferFrom()} 与
 * {@code writeByteBufferFrom()}。
 *
 * @param <W> 写者类型，必须是 {@link Writer} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface WriterSource<W extends Writer> {

    /**
     * 获取写者实例。
     *
     * <p>
     * 每次调用应返回一个<b>全新的、可用的</b> {@link Writer} 实例。
     * 调用者负责关闭返回的写者。
     *
     * @return {@link Writer} 实例（不可为 null）
     * @throws IOException 如果发生 I/O 错误或资源不可访问
     */
    W getWriter() throws IOException;

    /**
     * 从数据源读取字符缓冲区并写入到当前写者中。
     *
     * <p>
     * 本方法负责获取写者并在写入完成后自动关闭。数据从 {@link BufferReader}
     * 中逐段读取 {@link CharBuffer} 并传输到写者。
     *
     * <p>
     * <b>命名说明：</b> 方法名包含 {@code CharBuffer} 以在同时实现
     * {@link OutputStreamSource} 的类中与字节方向的同类方法区分。
     *
     * <p>
     * <b>资源责任：</b>
     * <ul>
     *   <li>本方法负责获取和关闭 {@link Writer}</li>
     *   <li>{@code source} 的关闭由调用者负责</li>
     * </ul>
     *
     * @param source 数据来源，不可为 null
     * @param <E>    {@code source} 可能抛出的异常类型
     * @throws IOException 如果发生 I/O 错误
     * @throws E           如果数据源读取时抛出异常
     */
    default <E extends Throwable> void writeCharBufferFrom(@NonNull BufferReader<? super CharBuffer, ? extends E> source)
            throws IOException, E {
        try (W writer = getWriter()) {
            IOUtils.text().transfer(source, writer);
        }
    }

    /**
     * 将字符缓冲区的内容写入到当前写者中。
     *
     * <p>
     * 本方法负责获取写者并在写入完成后自动关闭。
     * 写入完成后，{@code content} 的位置（position）将被推进到末尾。
     *
     * <p>
     * <b>资源责任：</b> 本方法负责获取和关闭 {@link Writer}，调用者无需手动管理。
     *
     * @param content 待写入的字符缓冲区，不可为 null
     * @throws IOException 如果发生 I/O 错误
     */
    default void writeFrom(@NonNull CharBuffer content) throws IOException {
        try (W writer = getWriter()) {
            IOUtils.text().writeTo(content, writer);
        }
    }

    /**
     * 从指定的读者源读取内容并写入到当前写者中。
     *
     * <p>
     * 本方法负责获取写者，并委托 {@code source} 的读取逻辑完成数据获取。
     * 数据以 {@link CharBuffer} 形式从 {@code source} 逐段读取，
     * 通过 {@link Writer#append(CharBuffer)} 写入当前写者。
     *
     * <p>
     * <b>资源责任：</b>
     * <ul>
     *   <li>本方法负责获取和关闭 {@link Writer}</li>
     *   <li>{@code source} 负责获取和关闭其内部的 {@link Reader}</li>
     * </ul>
     *
     * @param source 数据来源，不可为 null
     * @param <R>    {@code source} 提供的读者类型
     * @throws IOException 如果发生 I/O 错误
     */
    default <R extends Reader> void writeFrom(@NonNull ReaderSource<R> source) throws IOException {
        try (W writer = getWriter()) {
            source.readCharBufferInto((c) -> writer.append(c));
        }
    }
}