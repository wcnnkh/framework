package run.soeasy.framework.io.source;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 输出流源接口，用于提供 {@link OutputStream} 实例。
 *
 * <p>
 * 这是一个函数式接口，将 {@link OutputStream} 的创建或获取逻辑进行封装与延迟。
 * 与 {@link InputStreamSource}、{@link ReaderSource}、{@link WriterSource}
 * 共同构成 IO 资源的方向性抽象。
 *
 * <p>
 * 本接口同时继承自 {@link WritableByteChannelSource}，意味着每个输出流源
 * 天然也是一个可写字节通道源——通过 {@link Channels#newChannel(OutputStream)}
 * 将流包装为 {@link WritableByteChannel}，且通道关闭时会联动关闭底层流。
 *
 * <p>
 * <b>设计意图：</b>
 * <ul>
 *   <li>将 {@link OutputStream} 的获取逻辑与消费逻辑解耦，支持延迟打开和自动关闭</li>
 *   <li>继承 {@link WritableByteChannelSource} 获得通道级别的写入、传输和编码能力</li>
 *   <li>提供从缓冲区或数据源写入内容的便捷方法（继承自父接口）</li>
 *   <li>支持通过 {@link #encode(ThrowingFunction)} 将输出流编码为字符写者</li>
 * </ul>
 *
 * <p>
 * <b>资源责任：</b>
 * <ul>
 *   <li>所有 {@code write...()} 方法均通过 try-with-resources 确保资源被正确关闭</li>
 *   <li>{@link #writableChannel()} 返回的通道在 {@code close()} 时会联动关闭底层输出流，
 *       调用者必须负责关闭返回的通道</li>
 * </ul>
 *
 * @param <O> 输出流类型，必须是 {@link OutputStream} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface OutputStreamSource<O extends OutputStream> extends WritableByteChannelSource<WritableByteChannel> {

    /**
     * 获取输出流实例。
     *
     * <p>
     * 每次调用应返回一个<b>全新的、可用的</b> {@link OutputStream} 实例。
     * 调用者负责关闭返回的输出流。
     *
     * @return 输出流实例（不可为 null）
     * @throws IOException 如果发生 I/O 错误或资源不可访问
     */
    @NonNull
    O getOutputStream() throws IOException;

    /**
     * 获取可写字节通道。
     *
     * <p>
     * 每次调用都会获取一个新的 {@link OutputStream} 并通过
     * {@link Channels#newChannel(OutputStream)} 将其包装为
     * {@link WritableByteChannel}。
     *
     * <p>
     * <b>关闭联动：</b> 返回的通道在 {@link WritableByteChannel#close()} 时会
     * <b>同时关闭底层输出流</b>。调用者<b>必须负责关闭</b>返回的通道，
     * 不应单独关闭底层流。
     *
     * @return 可写字节通道
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    default WritableByteChannel writableChannel() throws IOException {
        return Channels.newChannel(getOutputStream());
    }
}