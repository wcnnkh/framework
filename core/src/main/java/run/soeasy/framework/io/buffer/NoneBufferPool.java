package run.soeasy.framework.io.buffer;

import java.nio.Buffer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 空实现缓冲区池，【默认推荐实现】，不做任何对象复用。
 *
 * <p>
 * <b>核心行为：</b>
 * <ul>
 * <li>{@code get()}：每次都通过 supplier 创建全新的缓冲区；</li>
 * <li>{@code close()}：空操作，不回收、不复用缓冲区，缓冲区交给 GC / Cleaner 自动释放。</li>
 * </ul>
 *
 * <p>
 * <b>适用场景（绝大多数业务首选）：</b>
 * <ul>
 * <li>HeapByteBuffer（堆内缓冲区）：JVM数组分配、新生代GC效率很高，池化收益极低，优先选本实现；</li>
 * <li>DirectByteBuffer（堆外缓冲区）低频分配场景；</li>
 * <li>不想承担对象池的复杂度、内存泄漏、归还校验、跨线程约束等风险；</li>
 * <li>面向 {@link BufferPool} 统一接口编程，预留扩展能力，后续如有需要可新增池化实现。</li>
 * </ul>
 *
 * <p>
 * <b>不适合场景：</b>
 * <ul>
 * <li>高频大量分配 DirectByteBuffer，allocateDirect 开销高、堆外内存回收滞后、内存毛刺严重；</li>
 * <li>需要业务层做缓冲区数量限流，防范堆外内存突刺导致OOM。</li>
 * </ul>
 *
 * <p>
 * <b>优势：</b>无锁、无状态、无归还校验、无内存泄漏风险、契约最简单；
 * <b>代价：</b>每次使用都会新建缓冲区，没有复用收益。
 * <p>
 * <b>行为说明：</b>close 不会调用 {@code buffer.clear()}，缓冲区不会复用，重置游标没有意义。
 *
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * // 指定大小 + 创建器
 * BufferPool<ByteBuffer> pool = new NoneBufferPool<>(4096, ByteBuffer::allocateDirect);
 * // 直接传入 supplier
 * BufferPool<ByteBuffer> pool2 = new NoneBufferPool<>(() -> ByteBuffer.allocate(1024));
 *
 * ByteBuffer buf = pool.get();
 * try {
 *     // 使用缓冲区
 * } finally {
 *     pool.close(buf);
 * }
 * }</pre>
 *
 * @param <B> 缓冲区类型，必须是 {@link Buffer} 的子类
 * @author soeasy.run
 * @see BufferPool
 */
@RequiredArgsConstructor
public final class NoneBufferPool<B extends Buffer> implements BufferPool<B> {
	@NonNull
	private final Supplier<? extends B> bufferCreator;

	/**
	 * 构造：固定缓冲区容量，使用 IntFunction 创建缓冲区。
	 *
	 * @param bufferSize 缓冲区容量
	 * @param creator    缓冲区创建函数，入参为容量
	 */
	public NoneBufferPool(int bufferSize, @NonNull IntFunction<? extends B> creator) {
		this(() -> creator.apply(bufferSize));
	}

	@Override
	public B get() {
		return bufferCreator.get();
	}

	/**
	 * 空归还实现，不执行任何复用逻辑。
	 * <p>缓冲区引用交由GC自动回收；DirectByteBuffer依靠Cleaner异步释放堆外内存。
	 * <p>本实现不会调用 {@code buffer.clear()}，缓冲区不再复用，重置游标无价值。
	 *
	 * @param source 待归还缓冲区
	 */
	@Override
	public void close(@NonNull B source) {
		// none，不复用，不执行clear，由GC/Cleaner负责资源释放
	}
}
