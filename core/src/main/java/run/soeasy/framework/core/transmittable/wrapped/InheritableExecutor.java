package run.soeasy.framework.core.transmittable.wrapped;

import java.util.concurrent.Executor;

import run.soeasy.framework.core.transmittable.Inheriter;

/**
 * 支持上下文传递的执行器包装器。
 * 该类继承自{@link Inheritable}，用于包装{@link Executor}实例，
 * 在提交任务时自动捕获和传递上下文状态，确保异步执行环境中的上下文一致性。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明上下文传递：无需修改原有任务代码，自动传递上下文</li>
 *   <li>执行器无缝适配：支持包装任何类型的Executor实现</li>
 *   <li>状态隔离保证：每个任务使用独立的上下文副本，避免相互干扰</li>
 *   <li>异常安全：通过try-finally确保上下文恢复，即使任务执行抛出异常</li>
 * </ul>
 *
 * <p>典型使用场景：
 * <pre>
 * // 创建支持上下文传递的Executor
 * InheritableExecutor&lt;Context, Backup, MyInheriter, ExecutorService&gt; inheritableExecutor = 
 *     new InheritableExecutor&lt;&gt;(
 *         Executors.newFixedThreadPool(10),
 *         contextInheriter
 *     );
 * 
 * // 提交任务，自动携带当前上下文
 * inheritableExecutor.execute(() -&gt; {
 *     // 任务逻辑，自动使用提交时的上下文
 * });
 * </pre>
 *
 * <p>设计考量：
 * <ul>
 *   <li>零侵入性：不改变原始Executor接口，保持使用方式一致性</li>
 *   <li>线程安全：上下文状态的捕获和恢复操作具有原子性</li>
 *   <li>性能优化：通过包装任务而非修改执行器内部实现，减少性能开销</li>
 * </ul>
 *
 * @param <A> 上下文捕获的数据类型
 * @param <B> 上下文备份的数据类型
 * @param <I> 继承器的具体类型，必须实现Inheriter接口
 * @param <W> 被包装的原始Executor类型
 * 
 * @see Inheritable
 * @see Executor
 * @see WrappedRunnable
 */
public class InheritableExecutor<A, B, I extends Inheriter<A, B>, W extends Executor> extends Inheritable<A, B, I, W>
        implements Executor {

    /**
     * 创建支持上下文传递的Executor包装器。
     * 
     * @param source 被包装的原始Executor实例，不可为null
     * @param inheriter 用于管理上下文的继承器，不可为null
     * @throws NullPointerException 如果source或inheriter为null
     */
    public InheritableExecutor(W source, I inheriter) {
        super(source, inheriter);
    }

    /**
     * 执行提交的Runnable任务，并自动包装任务以确保上下文传递。
     * 具体处理流程：
     * <ol>
     *   <li>将原始Runnable任务包装为{@link WrappedRunnable}</li>
     *   <li>通过被包装的Executor执行包装后的任务</li>
     * </ol>
     * 
     * @param command 待执行的Runnable任务
     */
    @Override
    public void execute(Runnable command) {
        getSource().execute(new WrappedRunnable<>(command, getInheriter()));
    }
}