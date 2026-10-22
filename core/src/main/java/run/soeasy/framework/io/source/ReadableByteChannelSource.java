package run.soeasy.framework.io.source;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferWriter;

/**
 * 可读字节通道源接口，用于提供 {@link ReadableByteChannel} 实例。
 *
 * <p>
 * 这是一个函数式接口，将 {@link ReadableByteChannel} 的创建或获取逻辑进行封装与延迟。
 * 与 {@link WritableByteChannelSource} 对称，本接口专注于<b>输入方向</b>的通道资源抽象。
 *
 * <p>
 * <b>设计意图：</b>
 * <ul>
 *   <li>将 {@link ReadableByteChannel} 的获取逻辑与消费逻辑解耦，支持延迟打开和自动关闭</li>
 *   <li>提供将通道内容读取为字节缓冲区、字节数组的便捷方法</li>
 *   <li>支持将通道内容传输到 {@link WritableByteChannelSource} 目标或 {@link BufferWriter} 消费者</li>
 *   <li>支持通过 {@link #decode(ThrowingFunction)} 将字节通道解码为字符读者源</li>
 * </ul>
 *
 * <p>
 * <b>资源责任：</b> 所有 {@code read...()} 方法均通过 try-with-resources 确保
 * {@link ReadableByteChannel} 被正确关闭；调用者无需（也不应）手动关闭传入的通道实例。
 *
 * <p>
 * <b>多重实现说明：</b> 当一个类同时实现 {@link ReadableByteChannelSource} 和
 * {@link InputStreamSource} 时，需通过方法名区分通道方向与流方向的读取，
 * 例如 {@code readByteBufferInto()}（通道源）与 {@code readByteBufferInto()}（流源，
 * 消费者类型不同）。
 *
 * @param <C> 可读字节通道类型，必须是 {@link ReadableByteChannel} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface ReadableByteChannelSource<C extends ReadableByteChannel> {

    /**
     * 获取可读字节通道实例。
     *
     * <p>
     * 每次调用应返回一个<b>全新的、可用的</b> {@link ReadableByteChannel} 实例。
     * 调用者负责关闭返回的通道。
     *
     * @return 可读字节通道实例（不可为 null）
     * @throws IOException 如果发生 I/O 错误或资源不可访问
     */
    C readableChannel() throws IOException;

    /**
     * 将通道内容以字节缓冲区形式传输到指定的消费者中。
     *
     * <p>
     * 本方法负责获取通道并在传输完成后自动关闭。数据以 {@link ByteBuffer}
     * 形式逐段从通道读取并传输给 {@code consumer}。
     *
     * <p>
     * <b>命名说明：</b> 方法名包含 {@code ByteBuffer} 以在同时实现
     * {@link ReaderSource} 的类中与字符方向的同类方法区分。
     *
     * <p>
     * <b>资源责任：</b>
     * <ul>
     *   <li>本方法负责获取和关闭 {@link ReadableByteChannel}</li>
     *   <li>{@code consumer} 的关闭由调用者负责</li>
     * </ul>
     *
     * @param consumer 数据消费者，不可为 null
     * @param <E>      {@code consumer} 可能抛出的异常类型
     * @throws IOException 如果发生 I/O 错误
     * @throws E           如果消费者处理数据时抛出异常
     */
    default <E extends Throwable> void readByteBufferInto(
            @NonNull BufferWriter<? super ByteBuffer, ? extends E> consumer) throws IOException, E {
        try (C channel = readableChannel()) {
            IOUtils.binary().readInto(channel, consumer);
        }
    }

    /**
     * 将当前通道源的内容写入到指定的可写通道源中。
     *
     * <p>
     * 本方法负责获取可读通道，并委托 {@code target} 获取可写通道，
     * 两者均在操作完成后自动关闭。数据以字节形式逐段从可读通道传输到可写通道。
     *
     * <p>
     * <b>资源责任：</b>
     * <ul>
     *   <li>本方法负责获取和关闭 {@link ReadableByteChannel}</li>
     *   <li>{@code target} 负责获取和关闭其内部的 {@link WritableByteChannel}</li>
     * </ul>
     *
     * @param target 写入目标，不可为 null
     * @param <W>    {@code target} 提供的可写通道类型，必须是 {@link WritableByteChannel} 的子类
     * @throws IOException 如果发生 I/O 错误
     */
    default <W extends WritableByteChannel> void writeToChannel(@NonNull WritableByteChannelSource<W> target) throws IOException {
        try (C reader = readableChannel()) {
            try (W writer = target.writableChannel()) {
                IOUtils.binary().readInto(reader, writer);
            }
        }
    }

    /**
     * 将全部内容读取为字节缓冲区。
     *
     * <p>
     * 本方法负责获取通道并在读取完成后自动关闭。
     * 返回的 {@link ByteBuffer} 为堆内缓冲区，其内容为读取到的全部字节数据。
     *
     * @return 包含全部内容的字节缓冲区
     * @throws IOException 如果发生 I/O 错误
     */
    default ByteBuffer readToByteBuffer() throws IOException {
        try (C channel = readableChannel()) {
            return ByteBuffer.wrap(IOUtils.binary().toByteArray(channel));
        }
    }

    /**
     * 将全部内容读取为字节数组。
     *
     * <p>
     * 本方法负责获取通道并在读取完成后自动关闭。
     *
     * @return 包含全部内容的字节数组
     * @throws IOException 如果发生 I/O 错误
     */
    default byte[] readToByteArray() throws IOException {
        try (C channel = readableChannel()) {
            return IOUtils.binary().toByteArray(channel);
        }
    }

    /**
     * 将可读通道解码为字符读者源。
     *
     * <p>
     * 通过指定的映射函数将 {@link ReadableByteChannel} 转换为 {@link Reader}，
     * 返回一个新的 {@link ReaderSource}。每次调用返回源的
     * {@link ReaderSource#getReader()} 时，都会获取一个新的通道并应用解码函数。
     * 调用者负责关闭从返回源中获取的 {@link Reader}。
     *
     * @param <R>    读者类型，必须是 {@link Reader} 的子类
     * @param mapper 将可读通道映射为读者的函数，不可为 null
     * @return 解码后的读者源
     */
    default <R extends Reader> ReaderSource<R> decode(
            @NonNull ThrowingFunction<? super C, ? extends R, ? extends IOException> mapper) {
        return () -> mapper.apply(readableChannel());
    }

    /**
     * 将可读通道按指定字符集名称解码为字符读者源。
     *
     * <p>
     * 通过 {@link Channels#newReader(ReadableByteChannel, String)} 创建
     * {@link Reader}，返回的 {@link ReaderSource} 每次获取读者时都会打开
     * 一个新的通道并按指定字符集进行解码。
     *
     * @param charsetName 字符集名称，不可为 null
     * @return 解码后的字符读者源
     */
    default ReaderSource<Reader> decode(@NonNull String charsetName) {
        return decode((input) -> Channels.newReader(input, charsetName));
    }
}