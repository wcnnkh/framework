package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

/**
 * 可抛出异常的函数包装器接口，用于包装{@link ThrowingFunction}实例，
 * 提供统一的委托操作机制。该接口继承自{@link ThrowingFunction}和{@link Wrapper}，
 * 允许将任意函数实例包装为具有相同接口的实例，同时保留源函数的所有功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有方法调用均转发至源函数实例，保持行为一致性</li>
 *   <li>类型安全：通过泛型参数确保包装前后的类型一致性</li>
 *   <li>可扩展接口：允许通过继承添加额外功能而不修改源实现</li>
 *   <li>函数组合：支持与其他函数进行组合操作（compose/andThen）</li>
 *   <li>异常转换：支持统一转换异常类型</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>为函数添加日志记录功能</li>
 *   <li>实现函数的访问控制或权限校验</li>
 *   <li>包装第三方函数实现以符合框架接口</li>
 *   <li>在不修改原始实现的情况下添加监控统计功能</li>
 *   <li>统一不同模块的异常处理逻辑</li>
 * </ul>
 *
 * @param <S> 函数的输入类型
 * @param <T> 函数的输出类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @param <W> 具体的包装器类型，需实现当前接口
 * @see ThrowingFunction
 * @see Wrapper
 */
@FunctionalInterface
public interface ThrowingFunctionWrapper<S, T, E extends Throwable, W extends ThrowingFunction<S, T, E>>
        extends ThrowingFunction<S, T, E>, Wrapper<W> {

    /**
     * 获取包装的源函数实例。
     * 该方法由{@link Wrapper}接口定义，是所有委托操作的基础。
     *
     * @return 被包装的源函数实例
     */
    @Override
    W getSource();

    /**
     * 组合当前函数与另一个函数，先执行before函数，再执行当前函数，委托给源函数的对应方法。
     *
     * @param <R>    组合后的输入类型
     * @param before 先执行的函数，不可为null
     * @return 组合后的函数实例
     */
    @Override
    default <R> ThrowingFunction<R, T, E> compose(
            @NonNull ThrowingFunction<? super R, ? extends S, ? extends E> before) {
        return getSource().compose(before);
    }

    /**
     * 组合当前函数与另一个函数，先执行当前函数，再执行after函数，委托给源函数的对应方法。
     *
     * @param <R>   组合后的输出类型
     * @param after 后执行的函数，不可为null
     * @return 组合后的函数实例
     */
    @Override
    default <R> ThrowingFunction<S, R, E> andThen(
            @NonNull ThrowingFunction<? super T, ? extends R, ? extends E> after) {
        return getSource().andThen(after);
    }

    /**
     * 转换异常类型，委托给源函数的对应方法。
     *
     * @param <R>               新的异常类型，必须是Throwable的子类
     * @param throwingMapper    异常转换函数，不可为null
     * @return 异常类型转换后的函数实例
     */
    @Override
    default <R extends Throwable> ThrowingFunction<S, T, R> throwing(
            @NonNull Function<? super E, ? extends R> throwingMapper) {
        return getSource().throwing(throwingMapper);
    }

    /**
     * 注册函数应用后的关闭回调，委托给源函数的对应方法。
     *
     * @param endpoint 关闭时执行的回调函数，不可为null
     * @return 注册回调后的函数实例
     */
    @Override
    default ThrowingFunction<S, T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
        return getSource().onClose(endpoint);
    }

    /**
     * 应用函数到输入参数，委托给源函数的{@link ThrowingFunction#apply}方法。
     *
     * @param source 输入参数
     * @return 函数应用结果
     * @throws E 可能抛出的指定类型异常
     */
    @Override
    default T apply(S source) throws E {
        return getSource().apply(source);
    }
}