package run.soeasy.framework.io.buffer;

import java.nio.Buffer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

import lombok.NonNull;

/**
 * 基于 Queue + Semaphore 的缓冲区对象池实现。
 *
 * <p>
 * <b>核心设计：</b>
 * <ul>
 * <li><b>职责分离：</b>{@link Queue} 负责缓存空闲缓冲区， {@link Semaphore}
 * 负责限制系统中同时存在的缓冲区总数（包括池内空闲和已借出使用中）。</li>
 * <li><b>严格对等：</b>每次 {@code get()} 消耗一个许可，每次 {@code close()}
 * 释放一个许可，确保对象总数不超上限。</li>
 * <li><b>可选容量限制：</b>构造时传入 {@code null} semaphore（或无参构造）表示不限制创建数量。</li>
 * <li><b>线程安全：</b>由底层 Queue 和 Semaphore 共同保证并发安全。</li>
 * </ul>
 *
 * <p>
 * <b>获取流程：</b>
 * <ol>
 * <li>消耗一个 semaphore 许可（如有）。</li>
 * <li>优先从队列 {@code poll()} 获取空闲缓冲区。</li>
 * <li>队列为空时，通过 supplier 创建新实例直接返回（不放入队列）。</li>
 * <li>创建失败时自动释放许可（补偿），原异常向上传播。</li>
 * </ol>
 *
 * <p>
 * <b>归还流程：</b>
 * <ol>
 * <li>调用 {@link Buffer#clear()} 重置缓冲区状态。</li>
 * <li>尝试 {@code offer} 回队列；队列满则抛出 {@link IllegalStateException}。</li>
 * <li>无论成功与否，在 {@code finally} 中释放一个 semaphore 许可。</li>
 * </ol>
 *
 * <p>
 * <b>异常行为：</b>
 * <table border="1">
 * <tr>
 * <th>场景</th>
 * <th>异常</th>
 * </tr>
 * <tr>
 * <td>{@code get()} 时许可耗尽</td>
 * <td>{@code IllegalStateException}</td>
 * </tr>
 * <tr>
 * <td>{@code close()} 时队列已满</td>
 * <td>{@code IllegalStateException}</td>
 * </tr>
 * <tr>
 * <td>{@code get()} 时创建失败</td>
 * <td>原异常 + 许可补偿释放</td>
 * </tr>
 * </table>
 *
 * <p>
 * <b>使用示例：</b>
 * 
 * <pre>{@code
 * // 1. 有界池：最多同时存在 10 个缓冲区
 * BufferPool<ByteBuffer> boundedPool = new QueueBufferPool<>(10, ByteBuffer::allocateDirect);
 *
 * // 2. 无界池：不限制创建数量
 * BufferPool<ByteBuffer> unboundedPool = new QueueBufferPool<>(ByteBuffer::allocate);
 *
 * // 3. 自定义队列 + 有界
 * BufferPool<ByteBuffer> customPool = new QueueBufferPool<>(new ArrayBlockingQueue<>(5), ByteBuffer::allocate,
 * 		new Semaphore(20));
 *
 * // 使用
 * ByteBuffer buf = boundedPool.get();
 * try {
 * 	// 使用 buf
 * } finally {
 * 	boundedPool.close(buf);
 * }
 * }</pre>
 *
 * @param <B> 缓冲区类型，必须是 {@link Buffer} 的子类
 * @author soeasy.run
 */
public class QueueBufferPool<B extends Buffer> implements BufferPool<B> {

	@NonNull
	private final Queue<B> bufferQueue;
	@NonNull
	private final Supplier<? extends B> bufferSupplier;
	private final Semaphore semaphore;

	/**
	 * 创建<b>有界、非阻塞</b>缓冲区池。
	 *
	 * <p>
	 * 使用 {@link ArrayBlockingQueue} 作为底层容器（容量 = 最大对象数）。 池满时 {@code get()}
	 * 抛异常，{@code close()} 队列满时也抛异常。
	 *
	 * @param maxPoolSize    最大缓冲区总数
	 * @param bufferSupplier 缓冲区创建器
	 */
	public QueueBufferPool(int maxPoolSize, @NonNull Supplier<? extends B> bufferSupplier) {
		this(new ArrayBlockingQueue<>(maxPoolSize), bufferSupplier, new Semaphore(maxPoolSize));
	}

	/**
	 * 创建<b>无界、非阻塞</b>缓冲区池（默认）。
	 *
	 * <p>
	 * 使用 {@link ConcurrentLinkedQueue} 作为底层容器，不限制创建数量。 适用于内存充足、追求极致性能的场景。
	 *
	 * @param bufferSupplier 缓冲区创建器
	 */
	public QueueBufferPool(@NonNull Supplier<? extends B> bufferSupplier) {
		this(new ConcurrentLinkedQueue<>(), bufferSupplier);
	}

	/**
	 * 创建自定义队列的缓冲区池（不限制创建数量）。
	 *
	 * @param bufferQueue    底层队列
	 * @param bufferSupplier 缓冲区创建器
	 */
	public QueueBufferPool(@NonNull Queue<B> bufferQueue, @NonNull Supplier<? extends B> bufferSupplier) {
		this(bufferQueue, bufferSupplier, null);
	}

	/**
	 * 全参构造。
	 *
	 * @param bufferQueue    底层队列
	 * @param bufferSupplier 缓冲区创建器
	 * @param semaphore      许可信号量（{@code null} 表示不限制创建数量）
	 */
	public QueueBufferPool(@NonNull Queue<B> bufferQueue, @NonNull Supplier<? extends B> bufferSupplier,
			Semaphore semaphore) {
		this.bufferQueue = bufferQueue;
		this.bufferSupplier = bufferSupplier;
		this.semaphore = semaphore;
	}

	/**
	 * 清空池中所有空闲缓冲区。
	 * <p>
	 * <b>注意：</b>不影响已借出到外部的缓冲区，也不影响 semaphore 许可数。
	 */
	public void clear() {
		bufferQueue.clear();
	}

	/**
	 * 归还缓冲区到池中。
	 *
	 * <p>
	 * 先重置缓冲区状态（{@link Buffer#clear()}），然后尝试放回队列。 队列满时抛出
	 * {@link IllegalStateException}。 无论成功与否，均会在 {@code finally} 中释放一个 semaphore 许可。
	 *
	 * @param buffer 待归还的非空缓冲区
	 * @throws IllegalStateException 如果底层队列已满，无法接收缓冲区
	 */
	@Override
	public void close(@NonNull B buffer) {
		try {
			buffer.clear();
			if (!bufferQueue.offer(buffer)) {
				throw new IllegalStateException("Buffer queue is full, cannot return buffer to pool");
			}
		} finally {
			release();
		}
	}

	/**
	 * 获取缓冲区实例。
	 *
	 * <p>
	 * 优先从队列获取空闲缓冲区；队列为空时创建新实例。 每次调用均会消耗一个 semaphore 许可（如有），创建失败时自动补偿释放。
	 *
	 * @return 可用的缓冲区实例
	 * @throws IllegalStateException 如果许可耗尽（有界模式下）
	 * @throws RuntimeException      如果缓冲区创建失败（原异常向上传播）
	 */
	@Override
	public B get() {
		acquire();
		try {
			B buffer = bufferQueue.poll();
			if (buffer != null) {
				return buffer;
			}
			return bufferSupplier.get();
		} catch (Throwable e) {
			release();
			throw e;
		}
	}

	private void acquire() {
		if (semaphore != null && !semaphore.tryAcquire()) {
			throw new IllegalStateException("Buffer pool is full, cannot add new buffer");
		}
	}

	private void release() {
		if (semaphore != null) {
			semaphore.release();
		}
	}
}