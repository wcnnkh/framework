package run.soeasy.framework.io.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.Text;
import run.soeasy.framework.io.buffer.BufferWriter;

/**
 * 读者源接口，用于提供 {@link Reader} 实例。
 *
 * <p>
 * 这是一个函数式接口，将 {@link Reader} 的创建或获取逻辑进行封装与延迟。
 * 与 {@link WriterSource} 对称，本接口专注于<b>输入方向</b>的资源抽象。
 *
 * <p>
 * <b>设计意图：</b>
 * <ul>
 *   <li>将 {@link Reader} 的获取逻辑与消费逻辑解耦，支持延迟打开和自动关闭</li>
 *   <li>提供将内容读取为字符串、字符缓冲区或逐行读取的便捷方法</li>
 *   <li>支持将内容传输到 {@link WriterSource} 等输出目标</li>
 * </ul>
 *
 * <p>
 * <b>资源责任：</b> 所有 {@code read...()} 方法均通过 try-with-resources 确保 {@link Reader}
 * 被正确关闭；调用者无需（也不应）手动关闭传入的 {@code Reader} 实例。
 *
 * <p>
 * <b>多重实现说明：</b> 当一个类同时实现 {@link ReaderSource} 和 {@link InputStreamSource} 时，
 * 需通过方法名区分字符方向与字节方向的读取，例如 {@code readCharBufferInto()} 与
 * {@code readByteBufferInto()}。
 *
 * @param <R> 读者类型，必须是 {@link Reader} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface ReaderSource<R extends Reader> {

    /**
     * 获取读者实例。
     *
     * <p>
     * 每次调用应返回一个<b>全新的、可用的</b> {@link Reader} 实例。
     * 调用者负责关闭返回的读者。
     *
     * @return {@link Reader} 实例（不可为 null）
     * @throws IOException 如果发生 I/O 错误或资源不可访问
     */
    R getReader() throws IOException;

    /**
     * 将全部内容读取为字符缓冲区。
     *
     * <p>
     * 本方法负责获取读者并在读取完成后自动关闭。
     * 返回的 {@link CharBuffer} 为堆内缓冲区，其内容为读取到的全部字符数据。
     *
     * @return 包含全部内容的字符缓冲区
     * @throws IOException 如果发生 I/O 错误
     */
    default CharBuffer readToCharBuffer() throws IOException {
        return CharBuffer.wrap(readToString());
    }

    /**
     * 将全部内容读取为字符串。
     *
     * <p>
     * 本方法负责获取读者并在读取完成后自动关闭。
     *
     * @return 包含全部内容的字符串
     * @throws IOException 如果发生 I/O 错误
     */
    default String readToString() throws IOException {
        try (R reader = getReader()) {
            return IOUtils.text().readToString(reader);
        }
    }

    /**
     * 将读者内容以字符缓冲区形式传输到指定的消费者中。
     *
     * <p>
     * 本方法负责获取读者并在传输完成后自动关闭。数据以 {@link CharBuffer}
     * 形式逐段从 {@link Readable#read(CharBuffer)} 读取并传输给 {@code consumer}。
     *
     * <p>
     * <b>命名说明：</b> 方法名包含 {@code CharBuffer} 以在同时实现
     * {@link InputStreamSource} 的类中与字节方向的同类方法区分。
     *
     * <p>
     * <b>资源责任：</b>
     * <ul>
     *   <li>本方法负责获取和关闭 {@link Reader}</li>
     *   <li>{@code consumer} 的关闭由调用者负责</li>
     * </ul>
     *
     * @param consumer 数据消费者，不可为 null
     * @param <E>      {@code consumer} 可能抛出的异常类型
     * @throws IOException 如果发生 I/O 错误
     * @throws E           如果消费者处理数据时抛出异常
     */
    default <E extends Throwable> void readCharBufferInto(
            @NonNull BufferWriter<? super CharBuffer, ? extends E> consumer) throws IOException, E {
        try (R reader = getReader()) {
            IOUtils.text().transfer(reader, consumer);
        }
    }

    /**
     * 将当前读者源的内容写入到指定的写者源中。
     *
     * <p>
     * 本方法通过 {@link Readable#read(CharBuffer)} 逐段读取字符数据，
     * 并委托 {@code target} 的写入逻辑完成输出。
     * 本方法负责获取读者，{@code target} 负责获取和关闭写者。
     *
     * @param target 写入目标，不可为 null
     * @param <W>    {@code target} 提供的写者类型
     * @throws IOException 如果发生 I/O 错误
     */
    default <W extends Writer> void writeTo(@NonNull WriterSource<W> target) throws IOException {
        try (R reader = getReader()) {
            target.writeCharBufferFrom(reader::read);
        }
    }

    /**
     * 将内容按行读取为字符串列表。
     *
     * <p>
     * 本方法内部使用 {@link BufferedReader} 包装读者，
     * 逐行读取并在完成后自动关闭所有资源。
     * 列表中的每一元素对应读者中的一行（不包含行终止符）。
     *
     * @return 包含所有行的字符串列表
     * @throws IOException 如果发生 I/O 错误
     */
    default List<String> readLines() throws IOException {
        try (R reader = getReader()) {
            try (BufferedReader bufferedReader = new BufferedReader(reader, Text.DEFAULT_CHAR_BUFFER_SIZE)) {
                return bufferedReader.lines().collect(Collectors.toList());
            }
        }
    }
}