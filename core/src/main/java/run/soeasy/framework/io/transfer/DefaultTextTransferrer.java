package run.soeasy.framework.io.transfer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 * 默认的字符串传输器实现。
 *
 * <p>本实现基于 {@link BufferTransferrer}，使用 {@link CharBuffer} 作为缓冲区，
 * 专门用于字符数据的传输。
 *
 * @see BufferTransferrer
 * @see CharBuffer
 */
@RequiredArgsConstructor
@Getter
public class DefaultTextTransferrer implements TextTransferrer {
    /**
     * 底层的缓冲区传输器，使用 CharBuffer 作为载体。
     */
    @NonNull
    private final BufferTransferrer<CharBuffer> bufferTransferrer;

    /**
     * 创建一个使用有界缓冲区池的字符串传输器。
     *
     * @param bufferCapacity 每个字符缓冲区的容量
     * @param maxPoolSize 缓冲区池的最大容量
     */
    public DefaultTextTransferrer(int bufferCapacity, int maxPoolSize) {
        this(new DefaultBufferTransferrer<>(maxPoolSize, bufferCapacity, CharBuffer::allocate));
    }

    /**
     * 创建一个使用无界缓冲区池的字符串传输器。
     *
     * @param bufferCapacity 每个字符缓冲区的容量
     */
    public DefaultTextTransferrer(int bufferCapacity) {
        this(new DefaultBufferTransferrer<>(bufferCapacity, CharBuffer::allocate));
    }

    /**
     * 执行字符串数据传输。
     *
     * <p>本方法将传输逻辑完全委托给底层的 {@link BufferTransferrer}。
     *
     * @param reader 字符缓冲区读取器
     * @param writer 字符缓冲区写入器
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public void transfer(
            @NonNull BufferReader<? super CharBuffer> reader,
            @NonNull BufferWriter<? super CharBuffer> writer
    ) throws IOException {
        bufferTransferrer.transfer(reader, writer);
    }
}