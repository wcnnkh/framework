package run.soeasy.framework.io.transfer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.Pool;
import run.soeasy.framework.io.buffer.BufferPool;
import run.soeasy.framework.io.buffer.QueueBufferPool;
import run.soeasy.framework.io.buffer.ThreadLocalBufferPool;

import java.io.IOException;
import java.nio.Buffer;
import java.util.function.IntFunction;

/**
 * 默认的缓冲区传输器实现。
 *
 * <p>本实现使用对象池来管理缓冲区，避免频繁创建和销毁缓冲区。
 *
 * <p><b>传输行为：</b>
 * <ul>
 *   <li>从对象池获取一个缓冲区</li>
 *   <li>循环调用 {@link BufferReader#read(Buffer)} 读取数据</li>
 *   <li>当读取返回的字节数大于0时，将数据写入 {@link BufferWriter}</li>
 *   <li>当读取返回0或-1时，循环结束</li>
 * </ul>
 *
 * <p><b>注意：</b>
 * 本实现不区分阻塞和非阻塞IO。在非阻塞IO中，如果 {@link BufferReader#read(Buffer)} 返回0，
 * 表示暂无数据，传输会立即结束。调用者需要根据实际情况决定是否重新调用 {@link #transfer}。
 *
 * @param <B> 缓冲区类型，必须是 {@link Buffer} 的子类
 */
@Getter
@RequiredArgsConstructor
public class DefaultBufferTransferrer<B extends Buffer> implements BufferTransferrer<B> {

    /**
     * 缓冲区池，用于重用缓冲区对象。
     */
    @NonNull
    private final BufferPool<B> bufferPool;

    /**
     * 创建一个使用有界缓冲区池的传输器。
     *
     * @param maxPoolSize    池的最大容量
     * @param bufferCapacity 每个缓冲区的容量
     * @param bufferCreator  缓冲区创建函数
     */
    public DefaultBufferTransferrer(int maxPoolSize, int bufferCapacity,
                                    IntFunction<? extends B> bufferCreator) {
        this(new QueueBufferPool<>(maxPoolSize, () -> bufferCreator.apply(bufferCapacity)));
    }

    /**
     * 创建一个使用{@link ThreadLocalBufferPool}的传输器。
     *
     * @param bufferCapacity 每个缓冲区的容量
     * @param bufferCreator  缓冲区创建函数
     */
    public DefaultBufferTransferrer(int bufferCapacity,
                                    IntFunction<? extends B> bufferCreator) {
        this(new ThreadLocalBufferPool<>(() -> bufferCreator.apply(bufferCapacity)));
    }

    /**
     * 执行缓冲区数据传输。
     *
     * <p>本方法会从缓冲区池中获取一个缓冲区，然后循环读取数据并写入目标，直到读取返回0或-1。
     *
     * <p>在传输过程中，如果 {@link BufferReader#read(Buffer)} 返回0，表示暂无数据（非阻塞模式），
     * 传输会立即结束。调用者需要自行处理这种情况，例如重新调用本方法进行传输。
     *
     * @param reader 缓冲区读取器，提供数据源
     * @param writer 缓冲区写入器，接收数据目标
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public void transfer(
            @NonNull BufferReader<? super B> reader,
            @NonNull BufferWriter<? super B> writer
    ) throws IOException {
        B buffer = bufferPool.get();
        try {
            // 当读取返回的字节数大于0时，继续循环
            while (reader.read(buffer) > 0) {
                buffer.flip();
                try {
                    writer.write(buffer);
                } finally {
                    buffer.clear();
                }
            }
        } finally {
            // 无论成功与否，归还缓冲区到池中
            bufferPool.close(buffer);
        }
    }
}