package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

/**
 * 可抛出异常的消费者包装器接口，用于包装{@link ThrowingConsumer}实例，
 * 提供统一的委托操作机制。该接口继承自{@link ThrowingConsumer}和{@link Wrapper}，
 * 允许将任意消费者实例包装为具有相同接口的实例，同时保留源消费者的所有功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有方法调用均转发至源消费者实例，保持行为一致性</li>
 *   <li>类型安全：通过泛型参数确保包装前后的类型一致性</li>
 *   <li>可扩展接口：允许通过继承添加额外功能而不修改源实现</li>
 *   <li>函数组合：支持与其他消费者进行组合操作（compose/andThen）</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>为消费者添加日志记录功能</li>
 *   <li>实现消费者的访问控制或权限校验</li>
 *   <li>包装第三方消费者实现以符合框架接口</li>
 *   <li>在不修改原始实现的情况下添加监控统计功能</li>
 *   <li>统一不同模块的异常处理逻辑</li>
 * </ul>
 *
 * @param <S> 消费操作的输入类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @param <W> 具体的包装器类型，需实现当前接口
 * @see ThrowingConsumer
 * @see Wrapper
 */
@FunctionalInterface
public interface ThrowingConsumerWrapper<S, E extends Throwable, W extends ThrowingConsumer<S, E>>
        extends ThrowingConsumer<S, E>, Wrapper<W> {

    /**
     * 获取包装的源消费者实例。
     * 该方法由{@link Wrapper}接口定义，是所有委托操作的基础。
     *
     * @return 被包装的源消费者实例
     */
    @Override
    W getSource();

    /**
     * 对输入进行类型映射后再应用当前消费操作，委托给源消费者的对应方法。
     *
     * @param <R>    映射后的输入类型
     * @param mapper 类型映射函数
     * @return 映射后的消费者实例
     */
    @Override
    default <R> ThrowingConsumer<R, E> map(ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
        return getSource().map(mapper);
    }

    /**
     * 组合消费操作，先执行before消费，再执行当前消费，委托给源消费者的对应方法。
     *
     * @param before 先执行的消费，不可为null
     * @return 组合后的消费者实例
     */
    @Override
    default ThrowingConsumer<S, E> compose(@NonNull ThrowingConsumer<? super S, ? extends E> before) {
        return getSource().compose(before);
    }

    /**
     * 组合消费操作，先执行当前消费，再执行after消费，委托给源消费者的对应方法。
     *
     * @param after 后执行的消费，不可为null
     * @return 组合后的消费者实例
     */
    @Override
    default ThrowingConsumer<S, E> andThen(@NonNull ThrowingConsumer<? super S, ? extends E> after) {
        return getSource().andThen(after);
    }

    /**
     * 转换异常类型，委托给源消费者的对应方法。
     *
     * @param <R>               新的异常类型，必须是Throwable的子类
     * @param throwingMapper    异常转换函数，不可为null
     * @return 异常类型转换后的消费者实例
     */
    @Override
    default <R extends Throwable> ThrowingConsumer<S, R> throwing(
            @NonNull Function<? super E, ? extends R> throwingMapper) {
        return getSource().throwing(throwingMapper);
    }

    /**
     * 注册消费后的关闭回调，委托给源消费者的对应方法。
     *
     * @param endpoint 关闭时执行的回调消费，不可为null
     * @return 注册回调后的消费者实例
     */
    @Override
    default ThrowingConsumer<S, E> onClose(@NonNull ThrowingConsumer<? super S, ? extends E> endpoint) {
        return getSource().onClose(endpoint);
    }

    /**
     * 执行消费操作，委托给源消费者的{@link ThrowingConsumer#accept}方法。
     *
     * @param source 输入参数
     * @throws E 可能抛出的指定类型异常
     */
    @Override
    default void accept(S source) throws E {
        getSource().accept(source);
    }
}