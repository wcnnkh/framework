package run.soeasy.framework.core.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 定时执行器服务包装器接口。
 * 该接口用于包装{@link ScheduledExecutorService}实例，提供统一的包装器抽象，
 * 允许在不修改原始执行器服务实现的前提下添加额外功能（如日志记录、监控统计等）。
 *
 * <p>核心特性：
 * <ul>
 *   <li>多层接口继承：继承自{@link ScheduledExecutorService}和{@link ExecutorServiceWrapper}，
 *                     形成完整的执行器包装体系</li>
 *   <li>默认方法实现：为所有定时任务方法提供委托实现，直接调用被包装对象的对应方法</li>
 *   <li>函数式扩展能力：作为函数式接口的子接口，支持通过lambda表达式实现自定义包装逻辑</li>
 *   <li>类型安全保证：通过泛型参数严格约束被包装对象的类型</li>
 * </ul>
 *
 * <p>典型使用场景：
 * <pre>
 * // 创建支持任务执行时间统计的包装器
 * ScheduledExecutorServiceWrapper&lt;ScheduledThreadPoolExecutor&gt; monitoringWrapper = executor -&gt; {
 *     return new ScheduledThreadPoolExecutor(
 *         executor.getCorePoolSize(),
 *         executor.getMaximumPoolSize(),
 *         executor.getKeepAliveTime(TimeUnit.MILLISECONDS),
 *         TimeUnit.MILLISECONDS,
 *         executor.getQueue(),
 *         executor.getThreadFactory()
 *     ) {
 *         private final StopWatch stopWatch = new StopWatch();
 *         
 *         &#64;Override
 *         public ScheduledFuture&lt;?&gt; schedule(Runnable command, long delay, TimeUnit unit) {
 *             stopWatch.start();
 *             return super.schedule(() -&gt; {
 *                 try {
 *                     command.run();
 *                 } finally {
 *                     stopWatch.stop();
 *                     System.out.println("任务执行耗时: " + stopWatch.getTotalTimeMillis() + "ms");
 *                 }
 *             }, delay, unit);
 *         }
 *     };
 * };
 * 
 * // 应用包装器
 * ScheduledExecutorService service = monitoringWrapper.wrap(Executors.newScheduledThreadPool(5));
 * </pre>
 *
 * <p>设计考量：
 * <ul>
 *   <li>非侵入式扩展：通过包装模式而非继承实现功能增强，不修改原始执行器代码</li>
 *   <li>接口隔离原则：分离定时执行器的核心功能与包装器功能，提高模块独立性</li>
 *   <li>默认实现优化：提供所有方法的默认委托实现，减少子类的样板代码</li>
 *   <li>扩展性设计：支持通过lambda表达式或匿名内部类快速实现自定义包装逻辑</li>
 * </ul>
 *
 * @param <W> 被包装的定时执行器服务类型，必须实现{@link ScheduledExecutorService}接口
 * 
 * @see ScheduledExecutorService
 * @see ExecutorServiceWrapper
 * @see java.util.concurrent.Executors
 */
public interface ScheduledExecutorServiceWrapper<W extends ScheduledExecutorService>
        extends ScheduledExecutorService, ExecutorServiceWrapper<W> {

    /**
     * 提交一个延迟执行的Callable任务，返回表示任务状态的ScheduledFuture。
     * 此方法直接委托给被包装的ScheduledExecutorService执行。
     * 
     * @param callable 待执行的Callable任务
     * @param delay 延迟执行时间
     * @param unit 时间单位
     * @param <V> 任务返回值类型
     * @return 封装任务执行状态的ScheduledFuture
     */
    @Override
    default <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return getSource().schedule(callable, delay, unit);
    }

    /**
     * 提交一个延迟执行的Runnable任务，返回表示任务状态的ScheduledFuture。
     * 此方法直接委托给被包装的ScheduledExecutorService执行。
     * 
     * @param command 待执行的Runnable任务
     * @param delay 延迟执行时间
     * @param unit 时间单位
     * @return 封装任务执行状态的ScheduledFuture
     */
    @Override
    default ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return getSource().schedule(command, delay, unit);
    }

    /**
     * 提交一个按固定速率执行的Runnable任务，返回表示任务状态的ScheduledFuture。
     * 此方法直接委托给被包装的ScheduledExecutorService执行。
     * 
     * @param command 待执行的Runnable任务
     * @param initialDelay 首次执行的延迟时间
     * @param period 执行间隔周期
     * @param unit 时间单位
     * @return 封装任务执行状态的ScheduledFuture
     */
    @Override
    default ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return getSource().scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * 提交一个按固定延迟执行的Runnable任务，返回表示任务状态的ScheduledFuture。
     * 此方法直接委托给被包装的ScheduledExecutorService执行。
     * 
     * @param command 待执行的Runnable任务
     * @param initialDelay 首次执行的延迟时间
     * @param delay 相邻两次执行之间的延迟
     * @param unit 时间单位
     * @return 封装任务执行状态的ScheduledFuture
     */
    @Override
    default ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return getSource().scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
}