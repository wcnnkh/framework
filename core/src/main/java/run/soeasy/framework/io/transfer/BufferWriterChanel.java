package run.soeasy.framework.io.transfer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;

/**
 * 基于 {@link BufferWriter} 的可写通道适配器。
 *
 * <p>该通道将 NIO 的 {@link WritableByteChannel} 接口适配到框架的 {@link BufferWriter} 抽象上，
 * 使得任何实现了 {@code BufferWriter<ByteBuffer>} 的对象都能以通道的形式被使用。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li><b>通道适配：</b>将 Channel 的写入语义转换为 BufferWriter 的写入语义</li>
 *   <li><b>位置追踪：</b>通过计算 Buffer 的 position 变化，准确返回写入的字节数</li>
 *   <li><b>生命周期管理：</b>支持关闭通道，防止对已关闭的资源进行操作</li>
 * </ul>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * // 创建一个写入到文件的 BufferWriter
 * BufferWriter<ByteBuffer> fileWriter = new FileBufferWriter("output.txt");
 *
 * // 适配为 WritableByteChannel
 * try (WritableByteChannel channel = new BufferWriterChanel(fileWriter)) {
 *     ByteBuffer buffer = ByteBuffer.wrap("Hello".getBytes());
 *     channel.write(buffer); // 实际调用 fileWriter.write(buffer)
 * }
 * }</pre>
 *
 * <p><b>重要约束：</b>
 * <ul>
 *   <li>通道关闭后调用 {@link #write(ByteBuffer)} 会抛出 {@link ClosedChannelException}</li>
 *   <li>写入的字节数通过 Buffer 的 position 变化计算得出，要求 {@link BufferWriter#write}
 *       必须正确推进 Buffer 的 position</li>
 *   <li>该类不是线程安全的，多线程并发访问需要外部同步</li>
 * </ul>
 *
 * @author soeasy.run
 * @see BufferWriter
 * @see java.nio.channels.WritableByteChannel
 */
@RequiredArgsConstructor
public final class BufferWriterChanel implements WritableByteChannel {

    @NonNull
    private final BufferWriter<? super ByteBuffer> bufferWriter;

    /**
     * 通道是否处于打开状态。
     */
    private boolean open = true;

    /**
     * 将数据从给定的缓冲区写入到底层 {@link BufferWriter}。
     *
     * <p>该方法会记录写入前缓冲区的 position，调用 writer 写入后，
     * 通过计算 position 的差值来确定实际写入的字节数。
     *
     * @param src 包含要写入数据的缓冲区
     * @return 写入的字节数，可能为 0
     * @throws ClosedChannelException 如果通道已关闭
     * @throws IOException 如果底层 writer 写入失败
     */
    @Override
    public int write(ByteBuffer src) throws IOException {
        if (!open) {
            throw new ClosedChannelException();
        }

        int pos = src.position();
        bufferWriter.write(src);
        return src.position() - pos;
    }

    /**
     * 返回通道是否处于打开状态。
     *
     * @return 如果通道处于打开状态则返回 true，否则返回 false
     */
    @Override
    public boolean isOpen() {
        return open;
    }

    /**
     * 关闭此通道。
     *
     * <p>关闭后，任何对该通道的写入操作都将抛出 {@link ClosedChannelException}。
     * 注意：此方法不会关闭底层的 {@link BufferWriter}，如果需要关闭 writer，
     * 请在调用此方法后手动关闭。
     *
     * @throws IOException 如果关闭过程中发生 I/O 错误
     */
    @Override
    public void close() throws IOException {
        open = false;
    }
}