package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 可抛出异常的函数接口，扩展了Java标准库的Function接口，允许在函数应用过程中抛出指定类型的异常。
 * 该接口是函数式接口，适用于需要处理可能抛出异常的函数转换场景，并提供了丰富的组合操作和异常处理方法。
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数式接口：可作为lambda表达式或方法引用的目标类型</li>
 *   <li>异常处理：允许在apply()方法中抛出指定类型的异常E</li>
 *   <li>函数组合：提供compose/andThen方法实现函数的前后组合</li>
 *   <li>异常转换：通过throwing方法实现异常类型的统一转换</li>
 *   <li>回调注册：通过onClose方法注册资源关闭时的回调操作</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要抛出特定异常的数据转换操作</li>
 *   <li>组合多个可能抛出异常的函数处理流程</li>
 *   <li>处理需要类型转换并可能抛出异常的复杂逻辑</li>
 *   <li>统一管理不同模块的异常类型</li>
 *   <li>资源处理完成后的回调操作注册</li>
 * </ul>
 *
 * @param <S> 函数的输入类型
 * @param <T> 函数的输出类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @see java.util.function.Function
 * @see ThrowingConsumer
 */
@FunctionalInterface
public interface ThrowingFunction<S, T, E extends Throwable> {

    /**
     * 获取恒等函数实例。
     * 该函数返回输入参数本身，不进行任何转换，且不会抛出异常，适用于需要默认恒等转换的场景。
     *
     * @param <T> 输入/输出类型
     * @param <E> 异常类型
     * @return 恒等函数实例
     */
    @SuppressWarnings("unchecked")
    public static <T, E extends Throwable> ThrowingFunction<T, T, E> identity() {
        return (ThrowingFunction<T, T, E>) IdentityThrowingFunction.INSTANCE;
    }

    /**
     * 组合当前函数与另一个函数，先执行before函数，再执行当前函数。
     * 返回的新函数会先将输入传递给before函数，再将其结果传递给当前函数。
     *
     * @param <R>    组合后的输入类型
     * @param before 先执行的函数，不可为null
     * @return 组合后的函数实例
     */
    default <R> ThrowingFunction<R, T, E> compose(
            @NonNull ThrowingFunction<? super R, ? extends S, ? extends E> before) {
        return new MappingThrowingFunction<>(before, this, Function.identity(), ThrowingConsumer.ignore());
    }

    /**
     * 组合当前函数与另一个函数，先执行当前函数，再执行after函数。
     * 返回的新函数会先将输入传递给当前函数，再将其结果传递给after函数。
     *
     * @param <R>   组合后的输出类型
     * @param after 后执行的函数，不可为null
     * @return 组合后的函数实例
     */
    default <R> ThrowingFunction<S, R, E> andThen(
            @NonNull ThrowingFunction<? super T, ? extends R, ? extends E> after) {
        return new MappingThrowingFunction<>(this, after, Function.identity(), ThrowingConsumer.ignore());
    }

    /**
     * 转换异常类型，返回新的ThrowingFunction实例。
     * 该方法允许将原始异常E转换为新的异常类型R，实现异常类型的统一处理。
     *
     * @param <R>               新的异常类型，必须是Throwable的子类
     * @param throwingMapper    异常转换函数，不可为null
     * @return 异常类型转换后的函数实例
     */
    default <R extends Throwable> ThrowingFunction<S, T, R> throwing(
            @NonNull Function<? super E, ? extends R> throwingMapper) {
        return new MappingThrowingFunction<>(this, identity(), throwingMapper, ThrowingConsumer.ignore());
    }

    /**
     * 注册函数应用后的关闭回调。
     * 当函数应用完成或抛出异常时，会调用指定的endpoint回调处理结果。
     *
     * @param endpoint 关闭时执行的回调函数，不可为null
     * @return 注册回调后的函数实例
     */
    default ThrowingFunction<S, T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
        return new MappingThrowingFunction<>(this, identity(), Function.identity(), endpoint);
    }

    /**
     * 应用函数到输入参数，返回结果，可能抛出异常E。
     * 该方法是函数式接口的抽象方法，必须由实现类提供具体实现。
     *
     * @param source 输入参数
     * @return 函数应用结果
     * @throws E 可能抛出的指定类型异常
     */
    T apply(S source) throws E;
}