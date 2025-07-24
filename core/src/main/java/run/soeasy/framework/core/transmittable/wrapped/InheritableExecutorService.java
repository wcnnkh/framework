package run.soeasy.framework.core.transmittable.wrapped;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.transmittable.Inheriter;

/**
 * 支持上下文传递的执行器服务包装器。
 * 该类继承自{@link InheritableExecutor}，用于包装{@link ExecutorService}实例，
 * 在提交任务时自动捕获和传递上下文状态，确保异步执行环境中的上下文一致性。
 *
 * <p>核心特性：
 * <ul>
 *   <li>全功能支持：实现ExecutorService的所有方法，保持原生API语义</li>
 *   <li>透明上下文传递：自动包装任务，无需修改业务逻辑</li>
 *   <li>批量操作支持：对invokeAll、invokeAny等批量方法提供上下文传递能力</li>
 *   <li>生命周期管理：完整继承被包装ExecutorService的生命周期控制</li>
 * </ul>
 *
 * <p>典型使用场景：
 * <pre>
 * // 创建支持上下文传递的ExecutorService
 * InheritableExecutorService&lt;Context, Backup, MyInheriter, ExecutorService&gt; inheritableService = 
 *     new InheritableExecutorService&lt;&gt;(
 *         Executors.newFixedThreadPool(10),
 *         contextInheriter
 *     );
 * 
 * // 提交Callable任务，自动携带当前上下文
 * Future&lt;String&gt; future = inheritableService.submit(() -> "result");
 * 
 * // 批量提交任务，所有任务共享相同上下文
 * List&lt;Future&lt;String&gt;&gt; futures = inheritableService.invokeAll(Collections.singletonList(
 *     () -> "task result"
 * ));
 * </pre>
 *
 * <p>设计考量：
 * <ul>
 *   <li>零侵入性：不改变原始ExecutorService接口，保持使用方式一致性</li>
 *   <li>线程安全：上下文状态的捕获和恢复操作具有原子性</li>
 *   <li>性能优化：通过任务包装而非修改执行器内部实现，减少性能开销</li>
 * </ul>
 *
 * @param <A> 上下文捕获的数据类型
 * @param <B> 上下文备份的数据类型
 * @param <I> 继承器的具体类型，必须实现Inheriter接口
 * @param <W> 被包装的原始ExecutorService类型
 * 
 * @see InheritableExecutor
 * @see ExecutorService
 * @see WrappedRunnable
 * @see WrappedCallable
 */
public class InheritableExecutorService<A, B, I extends Inheriter<A, B>, W extends ExecutorService>
        extends InheritableExecutor<A, B, I, W> implements ExecutorService {

    /**
     * 创建支持上下文传递的ExecutorService包装器。
     * 
     * @param source 被包装的原始ExecutorService实例，不可为null
     * @param inheriter 用于管理上下文的继承器，不可为null
     * @throws NullPointerException 如果source或inheriter为null
     */
    public InheritableExecutorService(W source, I inheriter) {
        super(source, inheriter);
    }

    /**
     * 阻塞当前线程，等待执行器服务终止。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @param timeout 等待终止的最大时间
     * @param unit 时间单位
     * @return 如果执行器服务在此期间终止，则返回true；否则返回false
     * @throws InterruptedException 如果等待期间被中断
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return getSource().awaitTermination(timeout, unit);
    }

    /**
     * 执行所有给定的任务，当所有任务完成时返回保持任务状态和结果的Future列表。
     * 每个任务都会被包装为{@link WrappedCallable}以确保上下文传递。
     * 
     * @param tasks 任务集合
     * @param <T> 任务返回类型
     * @return 表示任务的Future列表，列表顺序与集合迭代器所生成的顺序相同，
     *         每个任务都已完成
     * @throws InterruptedException 如果等待时被中断，在这种情况下未完成的任务将被取消
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return getSource().invokeAll(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
                : tasks.stream().map((e) -> new WrappedCallable<>(e, getInheriter())).collect(Collectors.toList()));
    }

    /**
     * 执行所有给定的任务，当所有任务完成或超时到期时，返回保持任务状态和结果的Future列表。
     * 每个任务都会被包装为{@link WrappedCallable}以确保上下文传递。
     * 
     * @param tasks 任务集合
     * @param timeout 等待的最大时间
     * @param unit 超时参数的时间单位
     * @param <T> 任务返回类型
     * @return 表示任务的Future列表，列表顺序与集合迭代器所生成的顺序相同。
     *         如果操作未超时，则每个任务都已完成。如果确实超时了，则某些任务可能未完成。
     * @throws InterruptedException 如果等待时被中断，在这种情况下未完成的任务将被取消
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return getSource().invokeAll(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
                : tasks.stream().map((e) -> new WrappedCallable<>(e, getInheriter())).collect(Collectors.toList()),
                timeout, unit);
    }

    /**
     * 执行给定的任务，返回已成功完成的任务的结果（即未抛出异常的任务）。
     * 每个任务都会被包装为{@link WrappedCallable}以确保上下文传递。
     * 
     * @param tasks 任务集合
     * @param <T> 任务返回类型
     * @return 某个任务执行的结果
     * @throws InterruptedException 如果等待时被中断
     * @throws ExecutionException 如果没有任务成功完成
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return getSource().invokeAny(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
                : tasks.stream().map((e) -> new WrappedCallable<>(e, getInheriter())).collect(Collectors.toList()));
    }

    /**
     * 执行给定的任务，返回已成功完成的任务的结果（即未抛出异常的任务），
     * 如果在所有任务成功完成之前超时到期，则抛出异常。
     * 每个任务都会被包装为{@link WrappedCallable}以确保上下文传递。
     * 
     * @param tasks 任务集合
     * @param timeout 等待的最大时间
     * @param unit 超时参数的时间单位
     * @param <T> 任务返回类型
     * @return 某个任务执行的结果
     * @throws InterruptedException 如果等待时被中断
     * @throws ExecutionException 如果没有任务成功完成
     * @throws TimeoutException 如果在所有任务完成前超时
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return getSource().invokeAny(CollectionUtils.isEmpty(tasks) ? Collections.emptyList()
                : tasks.stream().map((e) -> new WrappedCallable<>(e, getInheriter())).collect(Collectors.toList()),
                timeout, unit);
    }

    /**
     * 判断执行器服务是否已关闭。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @return 如果执行器服务已关闭，则返回true；否则返回false
     */
    @Override
    public boolean isShutdown() {
        return getSource().isShutdown();
    }

    /**
     * 判断执行器服务是否已完全终止。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @return 如果执行器服务已完全终止，则返回true；否则返回false
     */
    @Override
    public boolean isTerminated() {
        return getSource().isTerminated();
    }

    /**
     * 启动有序关闭，执行先前提交的任务，但不接受新任务。
     * 此方法直接委托给被包装的ExecutorService执行。
     */
    @Override
    public void shutdown() {
        getSource().shutdown();
    }

    /**
     * 尝试停止所有正在执行的活动任务，暂停处理等待中的任务，并返回等待执行的任务列表。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @return 等待执行的任务列表
     */
    @Override
    public List<Runnable> shutdownNow() {
        return getSource().shutdownNow();
    }

    /**
     * 提交一个返回值的任务用于执行，返回一个表示任务的未决结果的Future。
     * 任务会被包装为{@link WrappedCallable}以确保上下文传递。
     * 
     * @param task 要提交的任务
     * @param <T> 任务返回类型
     * @return 表示任务的Future，该Future的{@code get}方法在成功完成时将返回任务的结果
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return getSource().submit(new WrappedCallable<>(task, getInheriter()));
    }

    /**
     * 提交一个Runnable任务用于执行，并返回一个表示该任务的Future。
     * 任务会被包装为{@link WrappedRunnable}以确保上下文传递。
     * 
     * @param task 要提交的任务
     * @return 表示任务的Future，该Future的{@code get}方法在成功完成时将返回{@code null}
     */
    @Override
    public Future<?> submit(Runnable task) {
        return getSource().submit(new WrappedRunnable<>(task, getInheriter()));
    }

    /**
     * 提交一个Runnable任务用于执行，并返回一个表示该任务的Future，
     * 该Future的{@code get}方法在成功完成时将返回给定的结果。
     * 任务会被包装为{@link WrappedRunnable}以确保上下文传递。
     * 
     * @param task 要提交的任务
     * @param result 成功时要返回的结果
     * @param <T> 结果类型
     * @return 表示任务的Future，该Future的{@code get}方法在成功完成时将返回给定的结果
     */
    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return getSource().submit(new WrappedRunnable<>(task, getInheriter()), result);
    }
}