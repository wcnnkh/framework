package run.soeasy.framework.core.function;

import java.util.NoSuchElementException;

import lombok.NonNull;

/**
 * 可抛出异常的Optional包装器接口，用于包装{@link ThrowingOptional}实例，
 * 提供统一的委托操作机制。该接口继承自{@link ThrowingOptional}和{@link ThrowingSupplierWrapper}，
 * 允许将任意ThrowingOptional实例包装为具有相同接口的实例，同时保留源实例的所有功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有方法调用均转发至源ThrowingOptional实例，保持行为一致性</li>
 *   <li>类型安全：通过泛型参数确保包装前后的类型一致性</li>
 *   <li>空值安全：继承ThrowingOptional的安全值处理机制，避免NullPointerException</li>
 *   <li>异常处理：支持在值获取、转换等操作中抛出指定异常</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>为ThrowingOptional添加日志记录功能</li>
 *   <li>实现ThrowingOptional的访问控制或权限校验</li>
 *   <li>包装第三方ThrowingOptional实现以符合框架接口</li>
 *   <li>在不修改原始实现的情况下添加监控统计功能</li>
 *   <li>统一不同模块的异常处理逻辑</li>
 * </ul>
 *
 * @param <T> 包含的值的类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @param <W> 具体的包装器类型，需实现当前接口
 * @see ThrowingOptional
 * @see ThrowingSupplierWrapper
 */
@FunctionalInterface
public interface ThrowingOptionalWrapper<T, E extends Throwable, W extends ThrowingOptional<T, E>>
        extends ThrowingOptional<T, E>, ThrowingSupplierWrapper<T, E, W> {

    /**
     * 获取包装的源ThrowingOptional实例。
     * 该方法由{@link ThrowingSupplierWrapper}接口定义，是所有委托操作的基础。
     *
     * @return 被包装的源ThrowingOptional实例
     */
    @Override
    W getSource();

    /**
     * 获取值，如果值不存在则抛出NoSuchElementException，委托给源实例的对应方法。
     *
     * @return 存在的值
     * @throws E                可能抛出的异常
     * @throws NoSuchElementException 值不存在时抛出
     */
    @Override
    default T get() throws E, NoSuchElementException {
        return getSource().get();
    }

    /**
     * 对值进行扁平映射转换，委托给源实例的{@link ThrowingOptional#flatMap}方法。
     *
     * @param <R>    映射后的结果类型
     * @param <X>    可能抛出的新异常类型
     * @param mapper 映射函数，不可为null
     * @return 映射后的结果
     * @throws E 原始异常类型
     * @throws X 新的异常类型
     */
    @Override
    default <R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
            throws E, X {
        return getSource().flatMap(mapper);
    }

    /**
     * 如果值存在，则执行指定的消费操作，委托给源实例的对应方法。
     *
     * @param <X>    消费操作可能抛出的异常类型
     * @param consumer 消费函数，不可为null
     * @throws E 原始异常类型
     * @throws X 消费操作可能抛出的异常
     */
    @Override
    default <X extends Throwable> void ifPresent(ThrowingConsumer<? super T, ? extends X> consumer) throws E, X {
        getSource().ifPresent(consumer);
    }

    /**
     * 检查值是否存在，委托给源实例的{@link ThrowingOptional#isPresent}方法。
     *
     * @return 值存在返回true，否则返回false
     * @throws E 可能抛出的异常
     */
    @Override
    default boolean isPresent() throws E {
        return getSource().isPresent();
    }

    /**
     * 对值进行映射转换，返回新的ThrowingOptional，委托给源实例的{@link ThrowingOptional#map}方法。
     *
     * @param <R>    映射后的结果类型
     * @param mapper 映射函数，不可为null
     * @return 映射后的ThrowingOptional实例
     */
    @Override
    default <R> ThrowingOptional<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
        return getSource().map(mapper);
    }

    /**
     * 获取值，如果值不存在则返回指定的默认值，委托给源实例的{@link ThrowingOptional#orElse}方法。
     *
     * @param other 默认值
     * @return 值或默认值
     * @throws E 可能抛出的异常
     */
    @Override
    default T orElse(T other) throws E {
        return getSource().orElse(other);
    }

    /**
     * 获取值，如果值不存在则通过供应者获取默认值，委托给源实例的{@link ThrowingOptional#orElseGet}方法。
     *
     * @param <X>      供应者可能抛出的异常类型
     * @param suppler 供应者，不可为null
     * @return 值或供应者提供的默认值
     * @throws E 原始异常类型
     * @throws X 供应者可能抛出的异常
     */
    @Override
    default <X extends Throwable> T orElseGet(@NonNull ThrowingSupplier<? extends T, ? extends X> suppler) throws E, X {
        return getSource().orElseGet(suppler);
    }

    /**
     * 获取值，如果值不存在则抛出指定的异常，委托给源实例的{@link ThrowingOptional#orElseThrow}方法。
     *
     * @param <X>               要抛出的异常类型
     * @param exceptionSupplier 异常供应者，不可为null
     * @return 存在的值
     * @throws E 原始异常类型
     * @throws X 值不存在时抛出的异常
     */
    @Override
    default <X extends Throwable> T orElseThrow(ThrowingSupplier<? extends X, ? extends X> exceptionSupplier)
            throws E, X {
        return getSource().orElseThrow(exceptionSupplier);
    }

    /**
     * 返回自身，用于链式调用时保持接口一致性，委托给源实例的对应方法。
     *
     * @return 当前ThrowingOptional实例
     */
    @Override
    default ThrowingOptional<T, E> optional() {
        return getSource().optional();
    }
}