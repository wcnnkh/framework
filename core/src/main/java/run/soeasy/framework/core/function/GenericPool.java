package run.soeasy.framework.core.function;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 通用无界对象池核心实现
 * <p>实现 {@link Pool} 接口，提供通用的对象池化管理能力，支持自定义对象创建与回收逻辑，核心特性如下：
 * <ul>
 *     <li>无界空闲队列：基于 {@link LinkedBlockingQueue} 实现空闲对象存储，无空闲对象数量限制，支持无限缓存</li>
 *     <li>线程安全：队列天然支持多线程并发存取，无需额外加锁，可直接在多线程环境下安全使用</li>
 *     <li>优先复用：获取对象时优先从空闲队列中复用已有实例，无可用实例时再创建新对象，减少对象创建开销</li>
 *     <li>自定义逻辑：支持自定义对象供应商（创建新对象）和关闭处理器（对象回收/状态重置），灵活性极高</li>
 *     <li>异常声明：支持自定义受检异常类型，贴合不同业务场景的异常抛出需求</li>
 * </ul>
 *
 * @param <T> 池化对象的类型（任意可复用的对象类型，如数组、Buffer、连接等）
 * @param <E> 池操作过程中可能抛出的受检异常类型（继承自 {@link Exception}）
 * @author soeasy.run
 * @see Pool
 * @see ThrowingSupplier
 * @see ThrowingConsumer
 * @see LinkedBlockingQueue
 */
@RequiredArgsConstructor
public class GenericPool<T, E extends Exception> implements Pool<T, E> {

    /**
     * 无界空闲对象队列
     * <p>用于存储所有可复用的空闲对象实例，底层实现为 {@link LinkedBlockingQueue}：
     * <ul>
     *     <li>无界特性：队列容量无上限，可缓存任意数量的空闲对象，不会因队列满导致阻塞</li>
     *     <li>线程安全：内置并发控制，支持多线程同时调用 {@link #get()} 和 {@link #close(Object)} 方法</li>
     *     <li>非阻塞存取：使用 {@link Queue#poll()} 和 {@link Queue#offer(Object)} 方法，无阻塞等待，提升性能</li>
     * </ul>
     */
    private final Queue<T> freeBufferQueue = new LinkedBlockingQueue<>();

    /**
     * 池化对象供应商
     * <p>用于在空闲队列为空时，创建新的池化对象实例，替代直接通过构造方法创建对象：
     * <ul>
     *     <li>非空约束：该供应商不可为 null，否则会触发 {@link NullPointerException}</li>
     *     <li>异常抛出：创建对象过程中可抛出自定义受检异常 {@link E}，贴合业务异常场景</li>
     *     <li>结果非空：要求供应商返回非空对象，否则 {@link #get()} 方法会触发空指针异常</li>
     * </ul>
     */
    @NonNull
    private final ThrowingSupplier<? extends T, ? extends E> supplier;

    /**
     * 池化对象关闭/状态重置处理器
     * <p>在对象归还到空闲队列之前执行，核心作用是重置对象状态或释放临时资源，确保对象复用安全：
     * <ul>
     *     <li>非空约束：该处理器不可为 null，否则会触发 {@link NullPointerException}</li>
     *     <li>异常抛出：处理对象过程中可抛出自定义受检异常 {@link E}，便于异常统一处理</li>
     *     <li>无返回值：仅执行状态重置/资源释放逻辑（如清空数组元素、重置 Buffer 标记位等）</li>
     * </ul>
     */
    @NonNull
    private final ThrowingConsumer<? super T, ? extends E> closeHandler;

    /**
     * 实现 {@link Pool#close(Object)} 方法：将对象归还到对象池（非销毁对象，用于后续复用）
     * <p>执行流程（原子性保障：队列操作线程安全，处理器执行逻辑需用户保证线程安全）：
     * 1.  调用 {@link #closeHandler} 处理待归还对象，完成状态重置或临时资源释放
     * 2.  将处理后的对象通过 {@link Queue#offer(Object)} 方法加入空闲队列，供后续 {@link #get()} 方法复用
     * <p>注意：该方法不会阻塞，即使队列无界，offer 方法始终返回 true，确保快速完成对象归还
     *
     * @param source 要归还的池化对象实例（不可为 null，否则会触发 {@link NullPointerException}）
     * @throws E 当 {@link #closeHandler} 处理对象时，抛出的自定义受检异常
     * @see Pool#close(Object)
     * @see ThrowingConsumer#accept(Object)
     */
    @Override
    public void close(@NonNull T source) throws E {
        closeHandler.accept(source);
        freeBufferQueue.offer(source);
    }

    /**
     * 实现 {@link Pool#get()} 方法：从对象池中获取一个可用的池化对象实例
     * <p>获取逻辑（线程安全，无阻塞）：
     * 1.  优先调用 {@link Queue#poll()} 方法从空闲队列中获取空闲对象（非阻塞，无可用时返回 null）
     * 2.  若空闲队列为空，调用 {@link #supplier} 创建新的对象实例
     * 3.  通过 {@link Objects#requireNonNull(Object)} 校验新创建的对象非空，确保返回可用对象
     * 4.  返回空闲对象或新创建的对象实例
     *
     * @return 可用的池化对象实例（非 null）
     * @throws E 当 {@link #supplier} 创建对象时，抛出的自定义受检异常
     * @throws NullPointerException 当 {@link #supplier} 返回 null 时，由 {@link Objects#requireNonNull(Object)} 触发该异常
     * @see Pool#get()
     * @see ThrowingSupplier#get()
     * @see Objects#requireNonNull(Object)
     */
    @Override
    public T get() throws E {
        T buffer = freeBufferQueue.poll();
        if (buffer == null) {
            buffer = Objects.requireNonNull(supplier.get());
        }
        return buffer;
    }
}