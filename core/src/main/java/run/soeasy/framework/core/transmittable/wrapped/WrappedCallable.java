package run.soeasy.framework.core.transmittable.wrapped;

import java.util.concurrent.Callable;

import run.soeasy.framework.core.transmittable.Inheriter;

/**
 * 支持上下文传递的可调用任务包装器。
 * 该类继承自{@link Captured}，用于包装{@link Callable}任务，
 * 在任务执行前后自动管理上下文状态的捕获、重放和恢复，
 * 确保上下文在异步执行环境中正确传递。
 *
 * <p>核心特性：
 * <ul>
 *   <li>上下文透明传递：在异步任务执行时自动携带上下文状态</li>
 *   <li>状态隔离保证：每个任务执行前后自动恢复上下文，避免相互干扰</li>
 *   <li>异常安全：通过try-finally确保上下文恢复，即使任务执行抛出异常</li>
 *   <li>泛型安全：支持任意返回类型的Callable任务</li>
 * </ul>
 *
 * <p>典型使用场景：
 * <pre>
 * WrappedCallable&lt;Context, Backup, MyInheriter, String, Exception, Callable&lt;String&gt;&gt; wrappedTask = 
 *     new WrappedCallable&lt;&gt;(
 *         () -> { return "task result"; },
 *         contextInheriter
 *     );
 * 
 * Future&lt;String&gt; future = executorService.submit(wrappedTask);
 * </pre>
 *
 * <p>设计考量：
 * <ul>
 *   <li>线程池兼容性：可无缝集成现有线程池，无需修改执行框架</li>
 *   <li>最小侵入性：不改变原始Callable接口，保持使用方式一致性</li>
 *   <li>性能优化：通过预捕获上下文减少重复操作</li>
 * </ul>
 *
 * @param <A> 上下文捕获的数据类型
 * @param <B> 上下文备份的数据类型
 * @param <I> 继承器的具体类型，必须实现Inheriter接口
 * @param <T> Callable任务的返回类型
 * @param <E> Callable任务可能抛出的异常类型
 * @param <W> 被包装的原始Callable类型
 * 
 * @see Captured
 * @see Callable
 * @see Inheriter
 */
public class WrappedCallable<A, B, I extends Inheriter<A, B>, T, E extends Throwable, W extends Callable<? extends T>>
        extends Captured<A, B, I, W> implements Callable<T> {

    /**
     * 创建支持上下文传递的Callable任务包装器。
     * 
     * @param source 被包装的原始Callable任务，不可为null
     * @param inheriter 用于管理上下文的继承器，不可为null
     * @throws NullPointerException 如果source或inheriter为null
     */
    public WrappedCallable(W source, I inheriter) {
        super(source, inheriter);
    }

    /**
     * 执行被包装的Callable任务，并在执行前后自动管理上下文状态。
     * 具体执行流程：
     * <ol>
     *   <li>使用预捕获的上下文状态重放当前环境</li>
     *   <li>执行原始Callable任务</li>
     *   <li>无论任务是否抛出异常，始终恢复原始上下文状态</li>
     * </ol>
     * 
     * @return 任务执行结果
     * @throws Exception 如果任务执行过程中抛出异常
     */
    @Override
    public T call() throws Exception {
        B backup = getInheriter().replay(getCapture());
        try {
            return this.getSource().call();
        } finally {
            getInheriter().restore(backup);
        }
    };
}