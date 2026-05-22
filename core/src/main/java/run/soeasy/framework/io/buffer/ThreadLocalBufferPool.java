package run.soeasy.framework.io.buffer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.Buffer;
import java.util.function.Supplier;

/**
 * 线程本地缓冲区池。
 *
 * <p>基于 {@link ThreadLocal} 实现，为每个线程维护独立的缓冲区实例，
 * 避免多线程竞争，提升缓冲区复用效率。
 *
 * <p><b>设计意图：</b>
 * 在高并发 IO 场景下，减少 {@link Buffer} 的频繁创建与销毁开销，
 * 同时确保线程安全。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * Pool<ByteBuffer, IOException> pool = new ThreadLocalBufferPool<>(
 *     () -> ByteBuffer.allocate(8192)
 * );
 *
 * ByteBuffer buffer = pool.get();
 * try {
 *     // 使用 buffer
 * } finally {
 *     pool.close(buffer);
 * }
 * }</pre>
 *
 * @param <B> 缓冲区类型，必须是 {@link Buffer} 的子类
 * @author soeasy.run
 */
@RequiredArgsConstructor
public class ThreadLocalBufferPool<B extends Buffer> implements BufferPool<B> {

    /**
     * 线程本地缓冲区存储。
     */
    private final ThreadLocal<B> localBuffer = new ThreadLocal<>();

    /**
     * 缓冲区创建器，用于在线程首次请求时生成新实例。
     */
    @NonNull
    private final Supplier<? extends B> bufferSupplier;

    /**
     * 归还缓冲区到池中。
     *
     * <p>实现逻辑：
     * <ul>
     *   <li>重置缓冲区状态（调用 {@link Buffer#clear()}）</li>
     *   <li>清除线程本地引用，避免内存泄漏</li>
     * </ul>
     *
     * @param source 待归还的缓冲区实例
     */
    @Override
    public void close(@NonNull B source) {
        source.clear();
        localBuffer.remove();
    }

    /**
     * 获取线程绑定的缓冲区实例。
     *
     * <p>如果当前线程尚未持有缓冲区，则通过 {@link #bufferSupplier} 创建新实例。
     *
     * @return 线程绑定的缓冲区实例
     */
    @Override
    public B get() {
        B buffer = localBuffer.get();
        if (buffer == null) {
            buffer = bufferSupplier.get();
            localBuffer.set(buffer);
        }
        return buffer;
    }
}