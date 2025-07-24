package run.soeasy.framework.core.concurrent;

import java.util.concurrent.Executor;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 执行器包装函数式接口。
 * 该接口用于包装{@link Executor}实例，提供统一的包装器抽象，
 * 允许在不修改原始执行器实现的前提下添加额外功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数式接口设计：可作为lambda表达式或方法引用使用</li>
 *   <li>双重接口继承：同时继承{@link Executor}和{@link Wrapper}接口</li>
 *   <li>默认实现：提供{@code execute}方法的默认实现，直接委托给被包装对象</li>
 *   <li>类型安全：通过泛型参数确保被包装对象的类型一致性</li>
 * </ul>
 *
 * <p>典型使用场景：
 * <pre>
 * // 使用lambda表达式创建执行器包装器
 * ExecutorWrapper&lt;ThreadPoolExecutor&gt; wrappedExecutor = (ThreadPoolExecutor executor) -&gt; {
 *     // 添加监控逻辑
 *     System.out.println("提交任务到执行器: " + executor);
 *     return executor;
 * };
 * 
 * // 应用包装器
 * ExecutorService service = wrappedExecutor.wrap(Executors.newFixedThreadPool(10));
 * service.execute(() -&gt; System.out.println("任务执行"));
 * </pre>
 *
 * <p>设计考量：
 * <ul>
 *   <li>非侵入式扩展：通过包装模式而非继承实现功能扩展</li>
 *   <li>函数式编程支持：符合Java函数式接口规范，支持lambda表达式</li>
 *   <li>接口隔离：分离包装器功能与执行器功能，提高代码可维护性</li>
 * </ul>
 *
 * @param <W> 被包装的执行器类型，必须实现{@link Executor}接口
 * 
 * @see Executor
 * @see Wrapper
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ExecutorWrapper<W extends Executor> extends Executor, Wrapper<W> {
    /**
     * 执行提交的Runnable任务。
     * 该方法提供默认实现，直接调用被包装执行器的{@code execute}方法。
     * 实现类可根据需要重写此方法以添加额外逻辑。
     * 
     * @param command 待执行的Runnable任务
     */
    @Override
    default void execute(Runnable command) {
        getSource().execute(command);
    }
}