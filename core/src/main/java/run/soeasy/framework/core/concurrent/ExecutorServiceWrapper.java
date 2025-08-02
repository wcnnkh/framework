package run.soeasy.framework.core.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 执行器服务包装器函数式接口。
 * 该接口用于包装{@link ExecutorService}实例，提供统一的包装器抽象，
 * 允许在不修改原始执行器服务实现的前提下添加额外功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数式接口设计：可作为lambda表达式或方法引用使用</li>
 *   <li>多层接口继承：继承自{@link ExecutorWrapper}并扩展{@link ExecutorService}功能</li>
 *   <li>默认方法实现：为所有ExecutorService方法提供委托实现</li>
 *   <li>类型安全：通过泛型参数确保被包装对象的类型一致性</li>
 * </ul>
 *
 * <p>典型使用场景：
 * <pre>
 * // 创建支持监控的ExecutorService包装器
 * ExecutorServiceWrapper&lt;ThreadPoolExecutor&gt; monitoringWrapper = executor -&gt; {
 *     return new ThreadPoolExecutor(
 *         executor.getCorePoolSize(),
 *         executor.getMaximumPoolSize(),
 *         executor.getKeepAliveTime(TimeUnit.MILLISECONDS),
 *         TimeUnit.MILLISECONDS,
 *         executor.getQueue(),
 *         r -&gt; {
 *             Thread t = executor.getThreadFactory().newThread(r);
 *             t.setName("monitored-thread-" + t.getId());
 *             return t;
 *         }
 *     ) {
 *         &#64;Override
 *         protected void beforeExecute(Thread t, Runnable r) {
 *             System.out.println("开始执行任务: " + r);
 *             super.beforeExecute(t, r);
 *         }
 *     };
 * };
 * 
 * // 应用包装器
 * ExecutorService service = monitoringWrapper.wrap(Executors.newFixedThreadPool(10));
 * </pre>
 *
 * <p>设计考量：
 * <ul>
 *   <li>非侵入式扩展：通过包装模式而非继承实现功能扩展</li>
 *   <li>函数式编程支持：符合Java函数式接口规范，支持lambda表达式</li>
 *   <li>接口隔离：分离包装器功能与执行器功能，提高代码可维护性</li>
 *   <li>默认实现：为所有ExecutorService方法提供默认委托实现，简化实现类</li>
 * </ul>
 *
 * @param <W> 被包装的执行器服务类型，必须实现{@link ExecutorService}接口
 * 
 * @see ExecutorService
 * @see ExecutorWrapper
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ExecutorServiceWrapper<W extends ExecutorService> extends ExecutorService, ExecutorWrapper<W> {
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
    default boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return getSource().awaitTermination(timeout, unit);
    }

    /**
     * 执行所有给定的任务，当所有任务完成时返回保持任务状态和结果的Future列表。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @param tasks 任务集合
     * @param <T> 任务返回类型
     * @return 表示任务的Future列表，列表顺序与集合迭代器所生成的顺序相同，
     *         每个任务都已完成
     * @throws InterruptedException 如果等待时被中断，在这种情况下未完成的任务将被取消
     */
    @Override
    default <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return getSource().invokeAll(tasks);
    }

    /**
     * 执行所有给定的任务，当所有任务完成或超时到期时，返回保持任务状态和结果的Future列表。
     * 此方法直接委托给被包装的ExecutorService执行。
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
    default <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return getSource().invokeAll(tasks, timeout, unit);
    }

    /**
     * 执行给定的任务，返回已成功完成的任务的结果（即未抛出异常的任务）。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @param tasks 任务集合
     * @param <T> 任务返回类型
     * @return 某个任务执行的结果
     * @throws InterruptedException 如果等待时被中断
     * @throws ExecutionException 如果没有任务成功完成
     */
    @Override
    default <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return getSource().invokeAny(tasks);
    }

    /**
     * 执行给定的任务，返回已成功完成的任务的结果（即未抛出异常的任务），
     * 如果在所有任务成功完成之前超时到期，则抛出异常。
     * 此方法直接委托给被包装的ExecutorService执行。
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
    default <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return getSource().invokeAny(tasks, timeout, unit);
    }

    /**
     * 判断执行器服务是否已关闭。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @return 如果执行器服务已关闭，则返回true；否则返回false
     */
    @Override
    default boolean isShutdown() {
        return getSource().isShutdown();
    }

    /**
     * 判断执行器服务是否已完全终止。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @return 如果执行器服务已完全终止，则返回true；否则返回false
     */
    @Override
    default boolean isTerminated() {
        return getSource().isTerminated();
    }

    /**
     * 启动有序关闭，执行先前提交的任务，但不接受新任务。
     * 此方法直接委托给被包装的ExecutorService执行。
     */
    @Override
    default void shutdown() {
        getSource().shutdown();
    }

    /**
     * 尝试停止所有正在执行的活动任务，暂停处理等待中的任务，并返回等待执行的任务列表。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @return 等待执行的任务列表
     */
    @Override
    default List<Runnable> shutdownNow() {
        return getSource().shutdownNow();
    }

    /**
     * 提交一个返回值的任务用于执行，返回一个表示任务的未决结果的Future。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @param task 要提交的任务
     * @param <T> 任务返回类型
     * @return 表示任务的Future，该Future的{@code get}方法在成功完成时将返回任务的结果
     */
    @Override
    default <T> Future<T> submit(Callable<T> task) {
        return getSource().submit(task);
    }

    /**
     * 提交一个Runnable任务用于执行，并返回一个表示该任务的Future。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @param task 要提交的任务
     * @return 表示任务的Future，该Future的{@code get}方法在成功完成时将返回{@code null}
     */
    @Override
    default Future<?> submit(Runnable task) {
        return getSource().submit(task);
    }

    /**
     * 提交一个Runnable任务用于执行，并返回一个表示该任务的Future，
     * 该Future的{@code get}方法在成功完成时将返回给定的结果。
     * 此方法直接委托给被包装的ExecutorService执行。
     * 
     * @param task 要提交的任务
     * @param result 成功时要返回的结果
     * @param <T> 结果类型
     * @return 表示任务的Future，该Future的{@code get}方法在成功完成时将返回给定的结果
     */
    @Override
    default <T> Future<T> submit(Runnable task, T result) {
        return getSource().submit(task, result);
    }

    /**
     * 执行提交的Runnable任务。
     * 该方法直接委托给被包装执行器的{@code execute}方法。
     * 
     * @param command 待执行的Runnable任务
     */
    @Override
    default void execute(Runnable command) {
        getSource().execute(command);
    }
}