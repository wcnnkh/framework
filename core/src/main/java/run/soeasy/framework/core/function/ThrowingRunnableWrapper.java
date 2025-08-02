package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

/**
 * 可抛出异常的Runnable包装器接口，用于包装{@link ThrowingRunnable}实例，
 * 提供统一的委托操作机制。该接口继承自{@link ThrowingRunnable}和{@link Wrapper}，
 * 允许将任意可运行任务实例包装为具有相同接口的实例，同时保留源任务的所有功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有方法调用均转发至源任务实例，保持行为一致性</li>
 *   <li>类型安全：通过泛型参数确保包装前后的类型一致性</li>
 *   <li>任务组合：支持与其他任务进行顺序组合（compose/andThen）</li>
 *   <li>异常转换：支持统一转换异常类型</li>
 *   <li>资源管理：支持注册关闭回调函数</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>为任务添加日志记录功能</li>
 *   <li>实现任务的访问控制或权限校验</li>
 *   <li>包装第三方任务实现以符合框架接口</li>
 *   <li>在不修改原始实现的情况下添加监控统计功能</li>
 *   <li>统一不同模块的异常处理逻辑</li>
 * </ul>
 *
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @param <W> 具体的包装器类型，需实现当前接口
 * @see ThrowingRunnable
 * @see Wrapper
 */
@FunctionalInterface
public interface ThrowingRunnableWrapper<E extends Throwable, W extends ThrowingRunnable<E>>
        extends ThrowingRunnable<E>, Wrapper<W> {

    /**
     * 获取包装的源任务实例。
     * 该方法由{@link Wrapper}接口定义，是所有委托操作的基础。
     *
     * @return 被包装的源任务实例
     */
    @Override
    W getSource();

    /**
     * 组合当前任务与另一个任务，先执行before任务，再执行当前任务，委托给源任务的对应方法。
     *
     * @param before 先执行的任务，不可为null
     * @return 组合后的任务实例
     */
    @Override
    default ThrowingRunnable<E> compose(@NonNull ThrowingRunnable<? extends E> before) {
        return getSource().compose(before);
    }

    /**
     * 组合当前任务与另一个任务，先执行当前任务，再执行after任务，委托给源任务的对应方法。
     *
     * @param after 后执行的任务，不可为null
     * @return 组合后的任务实例
     */
    @Override
    default ThrowingRunnable<E> andThen(@NonNull ThrowingRunnable<? extends E> after) {
        return getSource().andThen(after);
    }

    /**
     * 转换异常类型，委托给源任务的对应方法。
     *
     * @param <R>               新的异常类型，必须是Throwable的子类
     * @param throwingMapper    异常转换函数，不可为null
     * @return 异常类型转换后的任务实例
     */
    @Override
    default <R extends Throwable> ThrowingRunnable<R> throwing(
            @NonNull Function<? super E, ? extends R> throwingMapper) {
        return getSource().throwing(throwingMapper);
    }

    /**
     * 注册任务执行后的关闭回调，委托给源任务的对应方法。
     *
     * @param endpoint 关闭时执行的回调任务，不可为null
     * @return 注册回调后的任务实例
     */
    @Override
    default ThrowingRunnable<E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
        return getSource().onClose(endpoint);
    }

    /**
     * 执行任务，委托给源任务的{@link ThrowingRunnable#run}方法。
     *
     * @throws E 可能抛出的指定类型异常
     */
    @Override
    default void run() throws E {
        getSource().run();
    }
}