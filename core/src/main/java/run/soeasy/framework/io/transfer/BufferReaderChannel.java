package run.soeasy.framework.io.transfer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ClosedChannelException;

/**
 * 基于 BufferReader 的可读字节通道适配器。
 *
 * <p>将 {@link BufferReader} 适配为 {@link ReadableByteChannel} 接口，
 * 使基于缓冲区的读取器可以作为 NIO Channel 使用。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * BufferReader<ByteBuffer> reader = buffer -> {
 *     // 填充数据
 *     return buffer.position();
 * };
 *
 * ReadableByteChannel channel = new BufferReaderChannel(reader);
 * ByteBuffer buffer = ByteBuffer.allocate(1024);
 * int bytesRead = channel.read(buffer);
 * }</pre>
 *
 * @author soeasy.run
 */
@RequiredArgsConstructor
public final class BufferReaderChannel implements ReadableByteChannel {

    /**
     * 底层缓冲区读取器。
     */
    @NonNull
    private final BufferReader<? super ByteBuffer> bufferReader;

    /**
     * 通道是否处于打开状态。
     */
    private boolean open = true;

    /**
     * 从通道读取数据到缓冲区。
     *
     * <p>该方法委托给 {@link BufferReader} 完成实际的读取操作。
     *
     * @param dst 目标缓冲区，不能为 null
     * @return 读取的字节数，如果到达流末尾则返回 -1
     * @throws IOException 如果读取过程中发生 I/O 错误
     * @throws ClosedChannelException 如果通道已关闭
     */
    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (!open) {
            throw new ClosedChannelException();
        }
        return bufferReader.read(dst);
    }

    /**
     * 判断通道是否处于打开状态。
     *
     * @return 如果通道处于打开状态则返回 true，否则返回 false
     */
    @Override
    public boolean isOpen() {
        return open;
    }

    /**
     * 关闭通道。
     *
     * <p>关闭后，任何读取操作都将抛出 {@link ClosedChannelException}。
     *
     * @throws IOException 如果关闭过程中发生错误
     */
    @Override
    public void close() throws IOException {
        open = false;
    }
}