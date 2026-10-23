package run.soeasy.framework.io.source;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;

/**
 * 可写字节通道源接口，用于提供 {@link WritableByteChannel} 实例。
 *
 * <p>
 * 这是一个函数式接口，将 {@link WritableByteChannel} 的创建或获取逻辑进行封装与延迟。
 * 与 {@link ReadableByteChannelSource} 对称，本接口专注于<b>输出方向</b>的通道资源抽象。
 *
 * <p>
 * <b>设计意图：</b>
 * <ul>
 *   <li>将 {@link WritableByteChannel} 的获取逻辑与消费逻辑解耦，支持延迟打开和自动关闭</li>
 *   <li>提供从 {@link ByteBuffer}、{@link BufferReader} 或 {@link ReadableByteChannelSource} 写入数据的便捷方法</li>
 *   <li>支持通过 {@link #encode(ThrowingFunction)} 将字节通道编码为字符写者源</li>
 * </ul>
 *
 * <p>
 * <b>资源责任：</b> 所有 {@code write...()} 方法均通过 try-with-resources 确保
 * {@link WritableByteChannel} 被正确关闭；调用者无需（也不应）手动关闭传入的通道实例。
 *
 * <p>
 * <b>多重实现说明：</b> 当一个类同时实现 {@link WritableByteChannelSource} 和
 * {@link OutputStreamSource} 时，需通过方法名区分通道方向与流方向的写入，
 * 例如 {@code writeByteBufferFrom()}（通道源）与 {@code writeByteBufferFrom()}（流源，
 * 读取器类型不同）。
 *
 * @param <C> 可写字节通道类型，必须是 {@link WritableByteChannel} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface WritableByteChannelSource<C extends WritableByteChannel> {

    /**
     * 获取可写字节通道实例。
     *
     * <p>
     * 每次调用应返回一个<b>全新的、可用的</b> {@link WritableByteChannel} 实例。
     * 调用者负责关闭返回的通道。
     *
     * @return 可写字节通道实例（不可为 null）
     * @throws IOException 如果发生 I/O 错误或资源不可访问
     */
    C writableChannel() throws IOException;

    /**
     * 从指定的可读通道源读取内容并写入到当前通道中。
     *
     * <p>
     * 本方法负责获取可写通道，并委托 {@code source} 获取可读通道，
     * 两者均在操作完成后自动关闭。数据以字节形式逐段从可读通道传输到当前通道。
     *
     * <p>
     * <b>资源责任：</b>
     * <ul>
     *   <li>本方法负责获取和关闭 {@link WritableByteChannel}</li>
     *   <li>{@code source} 负责获取和关闭其内部的 {@link ReadableByteChannel}</li>
     * </ul>
     *
     * @param source 数据来源，不可为 null
     * @param <R>    {@code source} 提供的可读通道类型
     * @throws IOException 如果发生 I/O 错误
     */
    default <R extends ReadableByteChannel> void writeFromChannel(ReadableByteChannelSource<R> source) throws IOException {
        try (R reader = source.readableChannel()) {
            try (C writer = writableChannel()) {
                IOUtils.binary().readInto(reader, writer);
            }
        }
    }

    /**
     * 将字节缓冲区的内容写入到当前通道中。
     *
     * <p>
     * 本方法负责获取通道并在写入完成后自动关闭。
     * 写入完成后，{@code source} 的位置（position）将被推进到末尾。
     *
     * <p>
     * <b>资源责任：</b> 本方法负责获取和关闭 {@link WritableByteChannel}，调用者无需手动管理。
     *
     * @param source 待写入的字节缓冲区，不可为 null
     * @throws IOException 如果发生 I/O 错误
     */
    default void writeFrom(@NonNull ByteBuffer source) throws IOException {
        try (C writer = writableChannel()) {
            writer.write(source);
        }
    }

    /**
     * 从数据源读取字节缓冲区并写入到当前通道中。
     *
     * <p>
     * 本方法负责获取通道并在写入完成后自动关闭。数据从 {@link BufferReader}
     * 中逐段读取 {@link ByteBuffer} 并传输到通道。
     *
     * <p>
     * <b>命名说明：</b> 方法名包含 {@code ByteBuffer} 以在同时实现
     * {@link WriterSource} 的类中与字符方向的同类方法区分。
     *
     * <p>
     * <b>资源责任：</b>
     * <ul>
     *   <li>本方法负责获取和关闭 {@link WritableByteChannel}</li>
     *   <li>{@code reader} 的关闭由调用者负责</li>
     * </ul>
     *
     * @param reader 数据来源，不可为 null
     * @param <E>    {@code reader} 可能抛出的异常类型
     * @throws IOException 如果发生 I/O 错误
     * @throws E           如果数据源读取时抛出异常
     */
    default <E extends Throwable> void writeByteBufferFrom(
            @NonNull BufferReader<? super ByteBuffer, ? extends E> reader) throws IOException, E {
        try (C writer = writableChannel()) {
            IOUtils.binary().transfer(reader, writer);
        }
    }

    /**
     * 将可写通道编码为字符写者源。
     *
     * <p>
     * 通过指定的映射函数将 {@link WritableByteChannel} 转换为 {@link Writer}，
     * 返回一个新的 {@link WriterSource}。每次调用返回源的
     * {@link WriterSource#getWriter()} 时，都会获取一个新的通道并应用编码函数。
     * 调用者负责关闭从返回源中获取的 {@link Writer}。
     *
     * @param <W>    写者类型，必须是 {@link Writer} 的子类
     * @param mapper 将可写通道映射为写者的函数，不可为 null
     * @return 编码后的写者源
     */
    default <W extends Writer> WriterSource<W> encode(
            @NonNull ThrowingFunction<? super C, ? extends W, ? extends IOException> mapper) {
        return () -> mapper.apply(writableChannel());
    }

    /**
     * 将可写通道按指定字符集名称编码为字符写者源。
     *
     * <p>
     * 通过 {@link Channels#newWriter(WritableByteChannel, String)} 创建
     * {@link Writer}，返回的 {@link WriterSource} 每次获取写者时都会打开
     * 一个新的通道并按指定字符集进行编码。
     *
     * @param charsetName 字符集名称，不可为 null
     * @return 编码后的字符写者源
     */
    default WriterSource<Writer> encode(@NonNull String charsetName) {
        return encode((input) -> Channels.newWriter(input, charsetName));
    }
}