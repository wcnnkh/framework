package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

/**
 * 可抛出异常的谓词包装器接口，用于包装{@link ThrowingPredicate}实例，
 * 提供统一的委托操作机制。该接口继承自{@link ThrowingPredicate}和{@link Wrapper}，
 * 允许将任意谓词实例包装为具有相同接口的实例，同时保留源谓词的所有功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有方法调用均转发至源谓词实例，保持行为一致性</li>
 *   <li>类型安全：通过泛型参数确保包装前后的类型一致性</li>
 *   <li>谓词组合：支持与其他谓词进行逻辑组合（and/or/negate）</li>
 *   <li>异常转换：支持统一转换异常类型</li>
 *   <li>输入映射：支持输入类型转换后再应用谓词判断</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>为谓词添加日志记录功能</li>
 *   <li>实现谓词的访问控制或权限校验</li>
 *   <li>包装第三方谓词实现以符合框架接口</li>
 *   <li>在不修改原始实现的情况下添加监控统计功能</li>
 *   <li>统一不同模块的异常处理逻辑</li>
 * </ul>
 *
 * @param <S> 谓词判断的输入类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @param <W> 具体的包装器类型，需实现当前接口
 * @see ThrowingPredicate
 * @see Wrapper
 */
@FunctionalInterface
public interface ThrowingPredicateWrapper<S, E extends Throwable, W extends ThrowingPredicate<S, E>>
        extends ThrowingPredicate<S, E>, Wrapper<W> {

    /**
     * 获取包装的源谓词实例。
     * 该方法由{@link Wrapper}接口定义，是所有委托操作的基础。
     *
     * @return 被包装的源谓词实例
     */
    @Override
    W getSource();

    /**
     * 对输入进行类型映射后再应用当前谓词，委托给源谓词的{@link ThrowingPredicate#map}方法。
     *
     * @param <R>    映射后的输入类型
     * @param mapper 类型映射函数，不可为null
     * @return 映射后的谓词实例
     */
    @Override
    default <R> ThrowingPredicate<R, E> map(@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
        return getSource().map(mapper);
    }

    /**
     * 组合当前谓词与另一个谓词，实现逻辑与操作，委托给源谓词的{@link ThrowingPredicate#and}方法。
     *
     * @param other 另一个谓词，不可为null
     * @return 组合后的谓词实例
     */
    @Override
    default ThrowingPredicate<S, E> and(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
        return getSource().and(other);
    }

    /**
     * 对当前谓词取反，实现逻辑非操作，委托给源谓词的{@link ThrowingPredicate#negate}方法。
     *
     * @return 取反后的谓词实例
     */
    @Override
    default ThrowingPredicate<S, E> negate() {
        return getSource().negate();
    }

    /**
     * 组合当前谓词与另一个谓词，实现逻辑或操作，委托给源谓词的{@link ThrowingPredicate#or}方法。
     *
     * @param other 另一个谓词，不可为null
     * @return 组合后的谓词实例
     */
    @Override
    default ThrowingPredicate<S, E> or(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
        return getSource().or(other);
    }

    /**
     * 转换异常类型，委托给源谓词的{@link ThrowingPredicate#throwing}方法。
     *
     * @param <R>               新的异常类型，必须是Throwable的子类
     * @param throwingMapper    异常转换函数，不可为null
     * @return 异常类型转换后的谓词实例
     */
    @Override
    default <R extends Throwable> ThrowingPredicate<S, R> throwing(
            @NonNull Function<? super E, ? extends R> throwingMapper) {
        return getSource().throwing(throwingMapper);
    }

    /**
     * 对输入进行判断，返回判断结果，委托给源谓词的{@link ThrowingPredicate#test}方法。
     *
     * @param source 输入参数
     * @return 判断结果，true或false
     * @throws E 可能抛出的指定类型异常
     */
    @Override
    default boolean test(S source) throws E {
        return getSource().test(source);
    }
}