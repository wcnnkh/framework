package run.soeasy.framework.core.exchange;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 定时可监听未来结果适配器，实现{@link ScheduledListenableFuture}接口。
 * 该适配器通过组合{@link ListenableFuture}和{@link Delayed}对象，
 * 将普通可监听未来结果转换为支持定时调度的可监听未来结果，实现接口方法的委托转发。
 *
 * <p>设计特点：
 * <ul>
 *   <li>委托模式：所有方法调用均转发给包装的{@link ListenableFuture}和{@link Delayed}实例</li>
 *   <li>类型安全：通过泛型参数确保结果类型一致性</li>
 *   <li>不可变性：包装的实例通过构造函数注入后不可变</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>将普通异步任务包装为定时调度任务</li>
 *   <li>为已有的{@link ListenableFuture}添加定时调度能力</li>
 *   <li>整合定时调度与结果监听功能</li>
 * </ul>
 *
 * @param <V> 异步操作返回的结果类型
 * 
 * @author soeasy.run
 * @see ScheduledListenableFuture
 * @see ListenableFuture
 * @see Delayed
 */
@RequiredArgsConstructor
public class ScheduledListenableFutureAdapter<V> implements ScheduledListenableFuture<V> {
    /** 被包装的可监听未来结果实例 */
    @NonNull
    private final ListenableFuture<V> listenableFuture;
    
    /** 被包装的延迟对象，提供定时调度能力 */
    @NonNull
    private final Delayed delayed;

    /**
     * 获取延迟执行的剩余时间
     * 
     * @param unit 时间单位
     * @return 转换为指定时间单位的剩余延迟时间
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return delayed.getDelay(unit);
    }

    /**
     * 与其他延迟对象比较执行顺序
     * 
     * @param o 待比较的延迟对象
     * @return 比较结果，符合{@link Delayed}接口规范
     */
    @Override
    public int compareTo(Delayed o) {
        return delayed.compareTo(o);
    }

    /**
     * 尝试取消异步操作
     * 
     * @param mayInterruptIfRunning 是否中断正在执行的任务
     * @return 如果操作成功取消返回true，否则返回false
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return listenableFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * 检查操作是否已被取消
     * 
     * @return 如果操作已被取消返回true，否则返回false
     */
    @Override
    public boolean isCancelled() {
        return listenableFuture.isCancelled();
    }

    /**
     * 检查操作是否已完成
     * 
     * @return 如果操作已完成返回true，否则返回false
     */
    @Override
    public boolean isDone() {
        return listenableFuture.isDone();
    }

    /**
     * 阻塞获取异步操作结果
     * 
     * @return 异步操作的结果
     * @throws InterruptedException 如果当前线程在等待时被中断
     * @throws ExecutionException 如果操作执行过程中发生异常
     */
    @Override
    public V get() throws InterruptedException, ExecutionException {
        return listenableFuture.get();
    }

    /**
     * 带超时控制的阻塞获取异步操作结果
     * 
     * @param timeout 最大等待时间
     * @param unit 时间单位
     * @return 异步操作的结果
     * @throws InterruptedException 如果当前线程在等待时被中断
     * @throws ExecutionException 如果操作执行过程中发生异常
     * @throws TimeoutException 如果等待时间超过timeout
     */
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return listenableFuture.get(timeout, unit);
    }

    /**
     * 注册事件监听器
     * 
     * @param listener 待注册的监听器
     * @return 注册操作的回执，用于取消注册
     */
    @Override
    public Operation registerListener(Listener<ListenableFuture<V>> listener) {
        return listenableFuture.registerListener(listener);
    }

    /**
     * 立即获取当前结果（如果可用）
     * 
     * @return 当前结果，如果操作未完成或失败则返回null
     */
    @Override
    public V getNow() {
        return listenableFuture.getNow();
    }

    /**
     * 检查操作是否可取消
     * 
     * @return 如果操作可取消返回true，否则返回false
     */
    @Override
    public boolean isCancellable() {
        return listenableFuture.isCancellable();
    }

    /**
     * 获取操作失败的原因
     * 
     * @return 失败原因，如果操作成功或未完成则返回null
     */
    @Override
    public Throwable cause() {
        return listenableFuture.cause();
    }

    /**
     * 检查操作是否成功完成
     * 
     * @return 如果操作成功完成返回true，否则返回false
     */
    @Override
    public boolean isSuccess() {
        return listenableFuture.isSuccess();
    }
}