package run.soeasy.framework.io.buffer;

import lombok.Getter;
import lombok.NonNull;

import java.nio.Buffer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * Default implementation of a buffer object pool.
 *
 * <p>This implementation adopts the <b>Delegate Pattern</b>, delegating capacity limits,
 * blocking behavior, and thread-safety strategies entirely to the underlying {@link Queue} implementation.
 *
 * <p><b>Design Highlights:</b>
 * <ul>
 *   <li><b>No state management:</b> No additional counters (e.g., createdCount) to avoid performance overhead and ABA issues.</li>
 *   <li><b>Configurable behavior:</b> Switch between "bounded/unbounded", "blocking/non-blocking" strategies by injecting different Queue implementations.</li>
 *   <li><b>Thread-safe:</b> Safety is fully guaranteed by the underlying Queue.</li>
 * </ul>
 *
 * <p><b>Usage Examples:</b>
 * <pre>{@code
 * // 1. Create a bounded, non-blocking pool with capacity 10
 * Pool<ByteBuffer, RuntimeException> blockingPool =
 *     new DefaultBufferPool<>(10, ByteBuffer::allocate);
 *
 * // 2. Create an unbounded, high-performance pool (suitable for memory-rich scenarios)
 * Pool<ByteBuffer, RuntimeException> nonBlockingPool =
 *     new DefaultBufferPool<>(ByteBuffer::allocate);
 * }</pre>
 *
 * @param <B> The type of buffer managed in the pool, must be a subclass of {@link Buffer}
 */
@Getter
public class QueueBufferPool<B extends Buffer>
        implements BufferPool<B> {
    private final Queue<B> bufferQueue;
    private final Supplier<? extends B> bufferSupplier;

    /**
     * Creates a <b>bounded, non-blocking</b> buffer pool.
     *
     * <p>Uses {@link ArrayBlockingQueue} as the underlying container.
     * When the pool is full, return operations will simply discard buffers (won't block).
     *
     * @param maxPoolSize    Maximum capacity of the pool
     * @param bufferSupplier Supplier used to create new buffers
     */
    public QueueBufferPool(int maxPoolSize,
                           Supplier<? extends B> bufferSupplier) {
        this(new ArrayBlockingQueue<>(maxPoolSize), bufferSupplier);
    }

    /**
     * Creates an <b>unbounded, non-blocking</b> buffer pool (default).
     *
     * <p>Uses {@link ConcurrentLinkedQueue} as the underlying container.
     * Theoretically limited only by JVM memory size.
     *
     * @param bufferSupplier Supplier used to create new buffers
     */
    public QueueBufferPool(Supplier<? extends B> bufferSupplier) {
        this(new ConcurrentLinkedQueue<>(), bufferSupplier);
    }

    /**
     * Custom constructor allowing full control over the underlying queue implementation.
     *
     * <p>For example, you can pass {@link java.util.concurrent.LinkedBlockingQueue} to achieve blocking behavior.
     *
     * @param bufferQueue    Queue used to store buffers
     * @param bufferSupplier Supplier used to create new buffers
     */
    public QueueBufferPool(Queue<B> bufferQueue,
                           Supplier<? extends B> bufferSupplier) {
        this.bufferQueue = bufferQueue;
        this.bufferSupplier = bufferSupplier;
    }

    /**
     * Returns a buffer to the pool.
     *
     * <p><b>Note:</b> This method calls {@link Buffer#clear()} before returning to reset the buffer state.
     * This ensures that the next time a buffer is retrieved from the pool, it is clean.
     *
     * <p>If the underlying queue is bounded and full, this operation will fail (depending on Queue implementation),
     * and the buffer will be discarded and recycled by GC.
     *
     * @param buffer The non-null buffer to return
     */
    @Override
    public void close(@NonNull B buffer) {
        // Must clear before returning, as this buffer has left the business thread and won't have concurrent read/write
        buffer.clear();
        // Attempt to put into queue; if queue is full (e.g., ArrayBlockingQueue), offer returns false and buffer is discarded
        bufferQueue.offer(buffer);
    }

    /**
     * Retrieves a buffer from the pool.
     *
     * <p>Retrieval flow:
     * <ol>
     *   <li>First attempt to poll an existing buffer from the queue.</li>
     *   <li>If the queue is empty, create a new buffer via the supplier and immediately place it into the queue.</li>
     * </ol>
     *
     * <p><b>Important:</b> This implementation does not limit creation count. Even with a bounded queue,
     * when the pool is empty, a new object will still be created to ensure business continuity.
     * However, if the queue is full when attempting to add the newly created buffer, an exception is thrown.
     *
     * @return An available buffer instance
     * @throws IllegalStateException If the pool is full and cannot accept new buffers
     */
    @Override
    public B get() {
        // 1. Try to get from queue
        B buffer = bufferQueue.poll();
        if (buffer != null) {
            return buffer;
        }

        // 2. Queue is empty, create new buffer and immediately put into queue
        buffer = bufferSupplier.get();
        if (!bufferQueue.offer(buffer)) {
            throw new IllegalStateException("Buffer pool is full, cannot add new buffer");
        }
        return buffer;
    }

    /**
     * Gets the number of idle buffers currently in the pool.
     *
     * <p>Note: For concurrent queues, this value is only approximate (snapshot).
     *
     * @return Number of buffers in the pool
     */
    public int getPoolSize() {
        return bufferQueue.size();
    }

    /**
     * Clears all buffers in the pool.
     */
    public void clear() {
        bufferQueue.clear();
    }
}