package run.soeasy.framework.io.transfer;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * 流式数据传输器接口，专门处理基于流的二进制数据传输。
 *
 * <p>该接口继承自 {@link BinaryTransferrer}，将传输抽象从 NIO Channel 层面
 * 下沉到传统 IO Stream 层面，适用于必须使用流语义的场景（如 Servlet 响应、
 * 压缩流、加密流等）。
 *
 * <p><b>设计特点：</b>
 * <ul>
 *   <li><b>流语义优先：</b>所有传输最终都转换为 {@link InputStream} 到
 *       {@link OutputStream} 的传输</li>
 *   <li><b>适配器模式：</b>通过 {@link Channels} 工具类将 Channel 适配为 Stream</li>
 *   <li><b>资源自动管理：</b>使用 try-with-resources 确保 Channel 资源正确释放</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>需要将 NIO Channel 数据写入传统 OutputStream（如 HTTP 响应体）</li>
 *   <li>需要对传输数据进行流式加工（如 GZIP 压缩、加密）</li>
 *   <li>兼容只支持 Stream API 的旧系统</li>
 * </ul>
 *
 * <p><b>示例：</b>
 * <pre>{@code
 * StreamTransferrer transferrer = new DefaultStreamTransferrer(4096);
 *
 * // Channel 到 Stream 的传输
 * transferrer.transfer(socketChannel, httpResponse.getOutputStream());
 *
 * // Buffer 到 Stream 的传输
 * transferrer.transfer(byteBufferReader, fileOutputStream);
 * }</pre>
 *
 * @author soeasy.run
 * @see BinaryTransferrer
 * @see BufferReaderChannel
 * @see BufferWriterChanel
 */
public interface StreamTransferrer extends BinaryTransferrer {

    /**
     * 从 {@link BufferReader} 传输数据到 {@link BufferWriter}。
     *
     * <p>通过适配器将 Buffer 抽象转换为 Channel，再委托给 Channel 传输方法。
     * 这是连接 Buffer 世界与 Channel 世界的桥梁。
     *
     * @param reader 数据读取器（非空）
     * @param writer 数据写入器（非空）
     * @throws IOException 传输过程中发生 I/O 错误
     */
    @Override
    default void transfer(@NonNull BufferReader<? super ByteBuffer> reader,
                          @NonNull BufferWriter<? super ByteBuffer> writer) throws IOException {
        try (BufferReaderChannel readerChannel = new BufferReaderChannel(reader);
             BufferWriterChanel writerChanel = new BufferWriterChanel(writer)) {
            this.transfer(readerChannel, writerChanel);
        }
    }

    /**
     * 从 {@link ReadableByteChannel} 传输数据到 {@link WritableByteChannel}。
     *
     * <p>注意：此方法将 NIO Channel 降级为传统 IO Stream 进行传输。
     * 这种转换会失去 NIO 的零拷贝优势，但能兼容必须使用流语义的场景。
     *
     * @param readable 可读通道（非空）
     * @param writable 可写通道（非空）
     * @throws IOException 传输过程中发生 I/O 错误
     */
    @Override
    default void transfer(@NonNull ReadableByteChannel readable, @NonNull WritableByteChannel writable) throws IOException {
        this.transfer(Channels.newInputStream(readable), Channels.newOutputStream(writable));
    }

    /**
     * 从输入流传输数据到输出流。
     *
     * <p>这是本接口的核心方法，所有其他重载方法最终都会委托给此方法。
     * 实现类应在此定义具体的流传输策略（如缓冲区大小、异常处理等）。
     *
     * @param input  源输入流（非空）
     * @param output 目标输出流（非空）
     * @throws IOException 传输过程中发生 I/O 错误
     */
    @Override
    void transfer(@NonNull InputStream input, @NonNull OutputStream output) throws IOException;
}