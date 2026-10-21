package run.soeasy.framework.io.source;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输入流源接口，用于提供 {@link InputStream} 实例。
 *
 * <p>
 * 将 {@link InputStream} 的创建或获取逻辑进行封装与延迟。
 * 与 {@link ReaderSource}、{@link WriterSource} 共同构成 IO 资源的方向性抽象。
 *
 * <p>
 * 本接口同时继承自 {@link ReadableByteChannelSource}，意味着每个输入流源
 * 天然也是一个可读字节通道源——通过 {@link Channels#newChannel(InputStream)}
 * 将流包装为 {@link ReadableByteChannel}，且通道关闭时会联动关闭底层流。
 *
 * <p>
 * <b>设计意图：</b>
 * <ul>
 *   <li>将 {@link InputStream} 的获取逻辑与消费逻辑解耦，支持延迟打开和自动关闭</li>
 *   <li>继承 {@link ReadableByteChannelSource} 获得通道级别的读取、传输和解码能力</li>
 *   <li>提供将内容读取为字节数组、缓冲区的便捷方法（继承自父接口）</li>
 *   <li>支持通过 {@link #decode(ThrowingFunction)} 将字节流解码为字符流</li>
 * </ul>
 *
 * <p>
 * <b>资源责任：</b>
 * <ul>
 *   <li>所有 {@code read...()} 方法均通过 try-with-resources 确保资源被正确关闭</li>
 *   <li>{@link #readableChannel()} 返回的通道在 {@code close()} 时会联动关闭底层输入流，
 *       调用者必须负责关闭返回的通道</li>
 * </ul>
 *
 * @param <I> 输入流类型，必须是 {@link InputStream} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface InputStreamSource<I extends InputStream> extends ReadableByteChannelSource<ReadableByteChannel> {

    /**
     * 获取输入流实例。
     *
     * <p>
     * 每次调用应返回一个<b>全新的、可用的</b> {@link InputStream} 实例。
     * 调用者负责关闭返回的输入流。
     *
     * @return 输入流实例（不可为 null）
     * @throws IOException 如果发生 I/O 错误或资源不可访问
     */
    @NonNull
    I getInputStream() throws IOException;

    /**
     * 获取可读字节通道。
     *
     * <p>
     * 每次调用都会获取一个新的 {@link InputStream} 并通过
     * {@link Channels#newChannel(InputStream)} 将其包装为
     * {@link ReadableByteChannel}。
     *
     * <p>
     * <b>关闭联动：</b> 返回的通道在 {@link ReadableByteChannel#close()} 时会
     * <b>同时关闭底层输入流</b>。调用者<b>必须负责关闭</b>返回的通道，
     * 不应单独关闭底层流。
     *
     * @return 可读字节通道
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }
}