package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 可抛出异常的谓词接口，扩展了Java标准库的Predicate接口，允许在测试操作中抛出指定类型的异常。
 * 该接口是函数式接口，适用于需要处理可能抛出异常的条件判断场景，并提供了丰富的组合操作方法。
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数式接口：可作为lambda表达式或方法引用的目标类型</li>
 *   <li>异常处理：允许在test()方法中抛出指定类型的异常E</li>
 *   <li>谓词组合：提供and/or/negate等方法实现复杂条件组合</li>
 *   <li>类型映射：通过map方法支持输入类型的转换</li>
 *   <li>异常转换：通过throwing方法实现异常类型的统一处理</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要抛出特定异常的条件判断（如权限校验）</li>
 *   <li>组合多个可能抛出异常的判断条件</li>
 *   <li>处理需要类型转换的复杂判断逻辑</li>
 *   <li>统一管理不同模块的异常类型</li>
 * </ul>
 *
 * @param <S> 谓词判断的输入类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @see java.util.function.Predicate
 * @see ThrowingFunction
 */
@FunctionalInterface
public interface ThrowingPredicate<S, E extends Throwable> {

    /**
     * 获取始终返回false的谓词实例。
     * 该实例的test()方法始终返回false，且不会抛出异常，适用于需要默认否定条件的场景。
     *
     * @param <S> 输入类型
     * @param <E> 异常类型
     * @return 始终返回false的谓词实例
     */
    @SuppressWarnings("unchecked")
    public static <S, E extends Throwable> ThrowingPredicate<S, E> alwaysFalse() {
        return (ThrowingPredicate<S, E>) AlwaysBooleanPredicat.FALSE;
    }

    /**
     * 获取始终返回true的谓词实例。
     * 该实例的test()方法始终返回true，且不会抛出异常，适用于需要默认肯定条件的场景。
     *
     * @param <S> 输入类型
     * @param <E> 异常类型
     * @return 始终返回true的谓词实例
     */
    @SuppressWarnings("unchecked")
    public static <S, E extends Throwable> ThrowingPredicate<S, E> alwaysTrue() {
        return (ThrowingPredicate<S, E>) AlwaysBooleanPredicat.TRUE;
    }

    /**
     * 对输入进行类型映射后再应用当前谓词。
     * 返回的新谓词会先通过mapper转换输入类型，再应用当前谓词的判断逻辑。
     *
     * @param <R>    映射后的输入类型
     * @param mapper 类型映射函数，不可为null
     * @return 映射后的谓词实例
     */
    default <R> ThrowingPredicate<R, E> map(@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
        return new MergedThrowingPredicate<>(mapper, this, Function.identity(), ThrowingConsumer.ignore());
    }

    /**
     * 组合当前谓词与另一个谓词，实现逻辑与操作。
     * 返回的新谓词只有在两个谓词都返回true时才返回true，否则返回false。
     *
     * @param other 另一个谓词，不可为null
     * @return 组合后的谓词实例
     */
    default ThrowingPredicate<S, E> and(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
        return (t) -> test(t) && other.test(t);
    }

    /**
     * 对当前谓词取反，实现逻辑非操作。
     * 返回的新谓词与当前谓词的判断结果相反。
     *
     * @return 取反后的谓词实例
     */
    default ThrowingPredicate<S, E> negate() {
        return (t) -> !test(t);
    }

    /**
     * 组合当前谓词与另一个谓词，实现逻辑或操作。
     * 返回的新谓词在任意一个谓词返回true时就返回true，否则返回false。
     *
     * @param other 另一个谓词，不可为null
     * @return 组合后的谓词实例
     */
    default ThrowingPredicate<S, E> or(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
        return (t) -> test(t) || other.test(t);
    }

    /**
     * 转换异常类型，返回新的谓词实例。
     * 该方法允许将原始异常E转换为新的异常类型R，实现异常类型的统一处理。
     *
     * @param <R>               新的异常类型，必须是Throwable的子类
     * @param throwingMapper    异常转换函数，不可为null
     * @return 异常类型转换后的谓词实例
     */
    default <R extends Throwable> ThrowingPredicate<S, R> throwing(
            @NonNull Function<? super E, ? extends R> throwingMapper) {
        return new MergedThrowingPredicate<>(ThrowingFunction.identity(), this, throwingMapper,
                ThrowingConsumer.ignore());
    }

    /**
     * 对输入进行判断，返回判断结果，可能抛出异常E。
     * 该方法是函数式接口的抽象方法，必须由实现类提供具体实现。
     *
     * @param source 输入参数
     * @return 判断结果，true或false
     * @throws E 可能抛出的指定类型异常
     */
    boolean test(S source) throws E;
}